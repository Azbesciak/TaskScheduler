package cs.put.ptsz.taskscheduler.output

import java.io.{File, FileOutputStream}

import cs.put.ptsz.taskscheduler.solver.{EvaluatedSolution, Instance, Result}
import cs.put.ptsz.taskscheduler.{InstanceParams, OptimumSource}

import scala.concurrent.duration.Duration
import scala.util.Properties

object OutputProducer {
	val OUTPUT_DIR_PROP = "outputDir"
	val DETAILS_PROP = "details"
	val SCHEDULING_PROP = "scheduling"
	val MEASURE_TIME_PROP = "measureTime"

	def consume(results: Array[Result]) = {
		val outputValues = results.map(r => make(r.instance, r.solution, r.duration))
		val padded: Array[String] = padValues(outputValues)
		val serialized = padded.mkString("\n").getBytes
		output(results).write(serialized)
	}

	def enable(property: String) = Properties.setProp(property, "true")

	private def output(results: Array[Result]) = {
		val instances = results.map(_.instance)
		val ids = instances.map(_.problem.id).distinct
		val hs = instances.map(_.h).distinct.map(_.toString.drop(2).toInt)
		val ns = instances.map(_.problem.tasks.length).distinct
		Properties.propOrNone(OUTPUT_DIR_PROP).map(p => {
			val file = getFile(ns, ids, hs, p)
			new FileOutputStream(file)
		}).getOrElse(Console.out)
	}

	private def getFile(ns: Array[Int], ids: Array[Int], hs: Array[Int], dir: String): File = {
		new File(dir).mkdirs()
		val format = s"$dir/n${ns.mkString(",")}k${ids.mkString(",")}h${hs.mkString(",")}"
		var file = new File(s"$format.txt")
		var index = 0
		while (file.exists()) {
			index += 1
			file = new File(s"$format($index).txt")
		}
		file
	}

	private def make(instance: Instance, solution: EvaluatedSolution, duration: Duration) = {
		val maybeDetails = onPropertySwitched(DETAILS_PROP, () => Array(
			s"n:${instance.problem.tasks.length}",
			s"k:${instance.problem.id}",
			s"h:${instance.h}")
		)
		val maybeDuration = onPropertySwitched(MEASURE_TIME_PROP, () => Array(s"TIME:${getTime(duration)}"))
		val maybeCompare = compareWithUpperBound(instance, solution)
		val maybeResult = getSolutionString(instance, solution)
		Array(maybeDetails, maybeCompare, maybeDuration, maybeResult).flatten.flatten
	}

	private def getSolutionString(instance: Instance, solution: EvaluatedSolution) = {
		onPropertySwitched(SCHEDULING_PROP, () => Array(s"${solution.cost.value} ${instance.h} ${solution.offset} ${solution.solution.tasks.map(_.task.id).mkString(" ")}"))
	}

	private def compareWithUpperBound(instance: Instance, solution: EvaluatedSolution): Option[Array[String]] = {
		if (!OptimumSource.canUse())
			return None
		val upperBound = OptimumSource.apply(InstanceParams(instance.problem.tasks.length, instance.problem.id, instance.h))
		val worseBy = (solution.cost.value - upperBound.value) / upperBound.value.toDouble * 100
		Some(Array(s"Fub:$upperBound",s"Fa:${solution.cost.value}", f"err:$worseBy%.2f%%"))
	}

	private def getTime(duration: Duration) =
		if (duration.toMillis < 10)
			s"${duration.toMicros} μs"
		else if (duration.toMillis < 10000)
			s"${duration.toMillis} ms"
		else
			s"${duration.toSeconds} s"

	private def onPropertySwitched(prop: String, block: () => Array[String]) =
		Properties.propOrNone(prop).filter(_ == "true").map(_ => block())

	private def padValues(outputValues: Array[Array[String]]) = {
		val maxLengths = outputValues.map(_.map(_.length)).transpose.map(_.max)
		val padded = outputValues
		 .map(_.zip(maxLengths)
			.map(t => t._1.padTo(t._2 + 1, ' '))
			.mkString(" | "))
		padded
	}
}
