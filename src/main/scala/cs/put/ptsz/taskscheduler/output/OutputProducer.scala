package cs.put.ptsz.taskscheduler.output

import cs.put.ptsz.taskscheduler.solver.{EvaluatedSolution, Instance}

object OutputProducer {
	def make(instance: Instance, solution: EvaluatedSolution) =
		s"${solution.cost.value} ${instance.h} ${solution.offset} ${solution.solution.tasks.map(_.task.id).mkString(" ")}"
}
