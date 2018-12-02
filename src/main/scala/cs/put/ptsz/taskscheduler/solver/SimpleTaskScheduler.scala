package cs.put.ptsz.taskscheduler.solver

import cs.put.ptsz.taskscheduler.cost.CostFunction
import cs.put.ptsz.taskscheduler.solver.mutator.TasksMutator
import cs.put.ptsz.taskscheduler.stopcondition.StopCondition


class SimpleTaskScheduler(
	private val instance: Instance,
	private val stopCondition: StopCondition,
	private val costFunction: CostFunction,
	private val mutator: TasksMutator
) extends TaskScheduler {
	private val tasks = instance.problem.tasks

	override def schedule(): EvaluatedSolution = {
		var best: EvaluatedSolution = null
		var currentSolution: EvaluatedSolution = null
		var bestTasks = tasks
		stopCondition.initialize()
		do {
			val currentTasks = mutator.mutate(bestTasks)
			val assigner = new OffSetFinder(costFunction, currentTasks)
			currentSolution = assigner.find()
			if (best == null || currentSolution.cost < best.cost) {
				best = currentSolution
				bestTasks = currentTasks
			}
		} while (stopCondition.canContinue(currentSolution) && mutator.canMutate())
		best
	}
}
