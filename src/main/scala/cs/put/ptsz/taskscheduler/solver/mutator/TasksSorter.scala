package cs.put.ptsz.taskscheduler.solver.mutator

import cs.put.ptsz.taskscheduler.solver.Task

object TasksSorter {

	def sortTasks(toDoBefore: Array[Task], toDoAfter: Array[Task]): (Array[Task], Array[Task]) = {
		val sortedToDoBefore = sortByEarlinessCost(toDoBefore)
		val sortedToDoAfter = sortByTardinessCost(toDoAfter).reverse
		(sortedToDoBefore, sortedToDoAfter)
	}

	def sortByEarlinessCost(tasks: Array[Task]): Array[Task] =
		tasks.sortBy(t => (t.earlinessCost / t.time.toDouble, t.earlinessCost, -t.time, -t.tardinessCost))

	def sortByTardinessCost(tasks: Array[Task]): Array[Task] =
		tasks.sortBy(t => (t.tardinessCost / t.time.toDouble, t.tardinessCost, -t.time, -t.earlinessCost))
}
