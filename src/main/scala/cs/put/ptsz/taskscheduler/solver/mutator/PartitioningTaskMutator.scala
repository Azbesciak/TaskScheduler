package cs.put.ptsz.taskscheduler.solver.mutator

import cs.put.ptsz.taskscheduler.solver.{Instance, Task}

class PartitioningTaskMutator(val instance: Instance) extends TasksMutator {
	private[this] var wasUsed = false

	override def mutate(tasks: Array[Task]): Array[Task] = {
		val (toDoBefore, toDoAfter) = tasks.partition(t => t.earlinessCost < t.tardinessCost)
		wasUsed = true
		val (sortedToDoBefore, sortedToDoAfter) = moveTasks(toDoBefore, toDoAfter)
		val beforeSum = sortedToDoBefore.map(_.time).sum
		val afterSum = sortedToDoAfter.map(_.time).sum
		val timeLeft = instance.dueTime - beforeSum
		val difference = (afterSum - beforeSum) / 10.0
		val changed = if (timeLeft < 0)
			fillTimeAfter(math.min(timeLeft, difference.toInt), sortedToDoBefore, sortedToDoAfter)
		else
			(sortedToDoBefore, sortedToDoAfter)
		changed._1 ++ changed._2
	}

	private def fillTimeAfter(timeLeft: Int, before: Array[Task], after: Array[Task]): (Array[Task], Array[Task]) = {
		var timeLeftVar = timeLeft
		val (leftBefore, toDoAfter) = sortByTardinessCost(before)
		 .partition(t => {
			 if (timeLeftVar + t.time <= 0) {
				 timeLeftVar += t.time
				 false
			 } else {
				 true
			 }
		 })
		moveTasks(leftBefore, toDoAfter ++ after)
	}

	private def moveTasks(toDoBefore: Array[Task], toDoAfter: Array[Task]): (Array[Task], Array[Task]) = {
		val sortedToDoBefore = sortByEarlinessCost(toDoBefore)
		val sortedToDoAfter = sortByTardinessCost(toDoAfter).reverse
		(sortedToDoBefore, sortedToDoAfter)
	}

	private def sortByEarlinessCost(tasks: Array[Task]) =
		tasks.sortBy(t => (t.earlinessCost / t.time.toDouble, t.earlinessCost, -t.time, -t.tardinessCost))
	private def sortByTardinessCost(tasks: Array[Task]) =
		tasks.sortBy(t => (t.tardinessCost / t.time.toDouble, t.tardinessCost, -t.time, -t.earlinessCost))
	override def canMutate(): Boolean = !wasUsed
}
