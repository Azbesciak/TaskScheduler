package cs.put.ptsz.taskscheduler.solver

import cs.put.ptsz.taskscheduler.cost.CostFunction
import cs.put.ptsz.taskscheduler.stopcondition.StopCondition

import scala.util.Random


class SimpleTaskScheduler(
	private val instance: Instance,
	private val stopCondition: StopCondition,
	private val costFunction: CostFunction
) extends TaskScheduler {
	private val tasks = instance.problem.tasks
	private val random = new Random(10)

	override def schedule(): EvaluatedSolution = {
		var best: EvaluatedSolution = null
		var currentSolution: EvaluatedSolution = null
		var currentTasks = tasks
		stopCondition.initialize()
		do {
			val assigner = new OffSetFinder(costFunction, currentTasks)
			currentSolution = assigner.find()
			if (best == null || currentSolution.cost < best.cost)
				best = currentSolution
			currentTasks = modify(currentTasks)
		} while (stopCondition.canContinue(currentSolution))
		best
	}

	private def modify(tasks: Array[Task]): Array[Task] = {
		random.shuffle(tasks.toSeq).toArray
	}
}
