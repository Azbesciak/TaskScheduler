package cs.put.ptsz.taskscheduler.solver.mutator

import cs.put.ptsz.taskscheduler.solver.Task


class AggregatedTaskMutator(private val mutators: TasksMutator*) extends TasksMutator {
	require(mutators.nonEmpty, () => "At least one mutator required")
	private var currentMutatorIndex = 0
	private var currentMutator = mutators(0)

	override def mutate(tasks: Array[Task]): Array[Task] = {
		require(canMutate(), () => "there is not more mutators")
		val mutated = currentMutator.mutate(tasks)
		updateMutatorIfNoMore
		mutated
	}

	private def updateMutatorIfNoMore = {
		while (currentMutator != null && !currentMutator.canMutate()) {
			if (currentMutatorIndex < mutators.size - 1){
				currentMutatorIndex += 1
				currentMutator = mutators(currentMutatorIndex)
			}
			else
				currentMutator = null
		}
	}

	override def canMutate(): Boolean = currentMutator != null && currentMutator.canMutate()
}