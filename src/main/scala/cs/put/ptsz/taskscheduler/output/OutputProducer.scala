package cs.put.ptsz.taskscheduler.output

import java.io.{File, FileOutputStream}

import cs.put.ptsz.taskscheduler.solver.{EvaluatedSolution, Instance, Result}

import scala.util.Properties

object OutputProducer {
	def consume(results: Array[Result]) = {
		val serialized = results.map(r => make(r.instance, r.solution)).mkString("\n").getBytes
		output().write(serialized)
	}

	private def output() = {
		Properties.propOrNone("outputFilePath").map(p => {
			new FileOutputStream(new File(p))
		}).getOrElse(Console.out)
	}

	private def make(instance: Instance, solution: EvaluatedSolution) =
		s"${solution.cost.value} ${instance.h} ${solution.offset} ${solution.solution.tasks.map(_.task.id).mkString(" ")}"
}
