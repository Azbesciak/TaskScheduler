package cs.put.ptsz.taskscheduler.output

import java.io.{File, FileOutputStream}

import cs.put.ptsz.taskscheduler.solver.{EvaluatedSolution, Instance, Result}

import scala.util.Properties

object OutputProducer {
	def consume(results: Array[Result]) = {
		val serialized = results.map(r => make(r.instance, r.solution)).mkString("\n").getBytes
		output(results).write(serialized)
	}

	private def output(results: Array[Result]) = {
		val instances = results.map(_.instance)
		val ids = instances.map(_.problem.id).distinct
		val hs = instances.map(_.h).distinct.map(_.toString.drop(2).toInt)
		val ns = instances.map(_.problem.tasks.length).distinct
		Properties.propOrNone("outputDir").map(p => {
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

	private def make(instance: Instance, solution: EvaluatedSolution) =
		s"${solution.cost.value} ${instance.h} ${solution.offset} ${solution.solution.tasks.map(_.task.id).mkString(" ")}"
}
