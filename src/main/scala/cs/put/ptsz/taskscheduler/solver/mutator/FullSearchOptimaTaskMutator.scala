package cs.put.ptsz.taskscheduler.solver.mutator

import cs.put.ptsz.taskscheduler.cost.CostFunction
import cs.put.ptsz.taskscheduler.solver.{OffSetFinder, Task}

class FullSearchOptimaTaskMutator(private val costFunction: CostFunction) extends TasksMutator {
	private var used = false

	override def mutate(tasks: Array[Task]): Array[Task] = {
		require(!used, "mutator already used")
		used = true
		tasks.indices.permutations
		 .map(_.map(tasks(_)))
		 .map(t => new OffSetFinder(costFunction, t.toArray).find())
		 .minBy(_.cost.value)
		 .solution.tasks
		 .map(_.task)
	}

	override def canMutate(): Boolean = !used
}
