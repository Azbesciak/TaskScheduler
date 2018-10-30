package cs.put.ptsz.taskscheduler.solver.mutator

import cs.put.ptsz.taskscheduler.solver.Task

class WeightedSortTasksMutator extends TasksMutator {
	private var wasUsed = false

	override def mutate(tasks: Array[Task]): Array[Task] = {
		wasUsed = true
		tasks.sortBy(t => (
		 (t.earlinessCost-t.tardinessCost) * math.max(t.tardinessCost, t.earlinessCost),
		 -math.max(t.tardinessCost, t.earlinessCost),
		 -t.time,
		 t.id)
		)
	}

	override def canMutate(): Boolean = !wasUsed
}
