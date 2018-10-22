package cs.put.ptsz.taskscheduler.solver

import cs.put.ptsz.taskscheduler.cost.CostFunction
import cs.put.ptsz.taskscheduler.stopcondition.StopCondition


class SimpleTaskScheduler(
	private val instance: Instance,
	private val stopCondition: StopCondition,
	private val costFunction: CostFunction
) extends TaskScheduler {
	private val tasks = instance.problem.tasks

	override def schedule(): EvaluatedSolution = {
		val assigner = new Assigner(costFunction, tasks)
		assigner.find()
	}
}
