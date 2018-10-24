package cs.put.ptsz.taskscheduler.solver.mutator

import cs.put.ptsz.taskscheduler.solver.{Instance, Task}


class SortTaskMutator(private val instance: Instance) extends TasksMutator {
	private val sortFunctions = getSortFun()

	override def mutate(tasks: Array[Task]): Array[Task] = {
		require(canMutate(), () => "no further mutations")
		val sortFun = sortFunctions.next
		tasks.sortBy(sortFun)
	}

	private def getSortFun[B](): Iterator[Task => (Int, Int, Int, Int)] = {
		Array(
			(task: Task) => task.earlinessCost,
			(task: Task) => -task.tardinessCost,
			(task: Task) => if (instance.h < 0.5) task.time else -task.time,
			(task: Task) => task.id
		).permutations
		 .map(a => a)
		 .map(a => (task: Task) => (a(0)(task), a(1)(task), a(2)(task), a(3)(task)))
	}

	override def canMutate(): Boolean = sortFunctions.hasNext
}