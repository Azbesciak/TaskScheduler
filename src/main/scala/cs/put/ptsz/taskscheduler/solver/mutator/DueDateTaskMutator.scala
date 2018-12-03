package cs.put.ptsz.taskscheduler.solver.mutator

import cs.put.ptsz.taskscheduler.solver.{EvaluatedSolution, Instance, Task}

class DueDateTaskMutator(
	private val instance: Instance,
	private val solutionEvaluator: Array[Task] => EvaluatedSolution
) extends TasksMutator {
	private val swappers = Array(
		BeforeToAfterTaskSwapper,
		AfterToBeforeTaskSwapper,
		null
	).iterator
	private var currentSwapper: TaskSwapper = NoOpTaskSwapper

	override def mutate(tasks: Array[Task]): Array[Task] = {
		require(currentSwapper != null, "no more swappers!")
		val oldSolution = solutionEvaluator(tasks)
		val indexOfCenterTask = oldSolution.solution.tasks.indexWhere(_.start > instance.dueTime)
		val (bef, aft) = if (indexOfCenterTask >= 0)
			tasks.splitAt(indexOfCenterTask)
		else
			(tasks, Array.empty[Task])
		val (bestBefore, bestAfter) = currentSwapper.swap(bef, aft)
		val newSolutionTasks = TasksSorter.sortByEarlinessCost(bestBefore) ++ TasksSorter.sortByTardinessCost(bestAfter).reverse
		val newSolution = solutionEvaluator(newSolutionTasks)
		if (!currentSwapper.canContinue(oldSolution, newSolution)) {
			currentSwapper = swappers.next()
		}
		newSolutionTasks
	}

	override def canMutate(): Boolean = currentSwapper != null
}

trait TaskSwapper {
	def swap(bef: Array[Task], after: Array[Task]): (Array[Task], Array[Task])

	def canContinue(solutionBefore: EvaluatedSolution, newSolution: EvaluatedSolution) =
		newSolution.cost < solutionBefore.cost
}

object NoOpTaskSwapper extends TaskSwapper {
	override def swap(bef: Array[Task], after: Array[Task]): (Array[Task], Array[Task]) = (bef, after)

	override def canContinue(solutionBefore: EvaluatedSolution, newSolution: EvaluatedSolution) = false
}

object BeforeToAfterTaskSwapper extends TaskSwapper {
	override def swap(bef: Array[Task], after: Array[Task]): (Array[Task], Array[Task]) = {
		if (bef.isEmpty)
			return (bef, after)
		val sortedByTardiness = TasksSorter.sortByTardinessCost(bef)
		(sortedByTardiness.drop(1), after :+ sortedByTardiness.head)
	}
}

object AfterToBeforeTaskSwapper extends TaskSwapper {
	override def swap(bef: Array[Task], after: Array[Task]): (Array[Task], Array[Task]) = {
		if (after.isEmpty)
			return (bef, after)
		val sortedByEarliness = TasksSorter.sortByEarlinessCost(after)
		(bef :+ sortedByEarliness.head, sortedByEarliness.drop(1))
	}
}