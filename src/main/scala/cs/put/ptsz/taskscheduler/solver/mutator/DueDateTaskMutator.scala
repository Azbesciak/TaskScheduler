package cs.put.ptsz.taskscheduler.solver.mutator

import cs.put.ptsz.taskscheduler.solver.{EvaluatedSolution, Instance, Task}

class DueDateTaskMutator(
	private val instance: Instance,
	private val solutionEvaluator: Array[Task] => EvaluatedSolution
) extends TasksMutator {
	private var wasUsed = false

	override def mutate(tasks: Array[Task]): Array[Task] = {
		wasUsed = true
		val solution = solutionEvaluator(tasks)
		val indexOfCenterTask = solution.solution.tasks.indexWhere(_.start > instance.dueTime)
		val (bef, aft) = if (indexOfCenterTask >= 0)
			tasks.splitAt(indexOfCenterTask)
		else
			(tasks, Array.empty[Task])
		TasksSorter.sortByEarlinessCost(bef) ++ TasksSorter.sortByTardinessCost(aft).reverse
	}

	override def canMutate(): Boolean = !wasUsed
}
