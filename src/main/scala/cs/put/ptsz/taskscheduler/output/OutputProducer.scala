package cs.put.ptsz.taskscheduler.output

import java.io.{File, FileOutputStream}

import cs.put.ptsz.taskscheduler.solver.{EvaluatedSolution, Instance, Result}

import scala.concurrent.duration.Duration
import scala.util.Properties

object OutputProducer {
	val OUTPUT_DIR_PROP = "outputDir"
	val DETAILS_PROP = "details"
	val MEASURE_TIME_PROP = "measureTime"

	def consume(results: Array[Result]) = {
		val serialized = results.map(r => make(r.instance, r.solution, r.duration)).mkString("\n").getBytes
		output(results).write(serialized)
	}

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
		val maybeDetails = onPropertySwitched(DETAILS_PROP, () => s"n:${instance.problem.tasks.length} k:${instance.problem.id} h:${instance.h}")
		val maybeDuration = onPropertySwitched(MEASURE_TIME_PROP, () => s"TIME: ${getTime(duration)}")
		val result = Some(s"${solution.cost.value} ${instance.h} ${solution.offset} ${solution.solution.tasks.map(_.task.id).mkString(" ")}")
		Array(maybeDetails, maybeDuration, result).flatten.mkString("\t| ")
	}

	private def getTime(duration: Duration) =
		if (duration.toMillis < 10)
			s"${duration.toNanos} ns"
		else if (duration.toMillis < 10000)
			s"${duration.toMillis} ms"
		else
			s"${duration.toSeconds} s"

	private def onPropertySwitched(prop: String, block: () => String) =
		Properties.propOrNone(prop).filter(_ == "true").map(_ => block())

	def enable(property: String) = Properties.setProp(property, "true")
}
