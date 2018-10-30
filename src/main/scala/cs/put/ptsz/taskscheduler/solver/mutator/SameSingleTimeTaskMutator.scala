package cs.put.ptsz.taskscheduler.solver.mutator

import cs.put.ptsz.taskscheduler.solver.Task


class SameSingleTimeTaskMutator extends TasksMutator {
	private var used = false

	override def mutate(tasks: Array[Task]): Array[Task] = {
		require(!used, "mutator already used")
		used = true
		tasks
	}

	override def canMutate(): Boolean = !used
}