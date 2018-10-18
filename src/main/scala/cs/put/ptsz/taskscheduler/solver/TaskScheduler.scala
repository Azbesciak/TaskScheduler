package cs.put.ptsz.taskscheduler.solver

import cs.put.ptsz.taskscheduler.cost.CostFunction
import cs.put.ptsz.taskscheduler.stopcondition.StopCondition


class TaskScheduler(
	private val instance: Instance,
	private val stopCondition: StopCondition,
	private val costFunction: CostFunction
) {
	private val tasks: Array[Task] = instance.problem.tasks

	def schedule(): EvaluatedSolution = {
		val assigner = new Assigner(costFunction, tasks)
		assigner.find()
	}
}
