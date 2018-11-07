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
//			fillTimeBefore(timeLeft, sortedToDoBefore, sortedToDoAfter)
		changed._1 ++ changed._2
	}

	private def fillTimeAfter(timeLeft: Int, before: Array[Task], after: Array[Task]): (Array[Task], Array[Task]) = {
		var timeLeftVar = timeLeft
		val (leftBefore, toDoAfter) = before.sortBy(t => (-t.time.toDouble / t.tardinessCost, -t.time, -t.tardinessCost))
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

	private def fillTimeBefore(timeLeft: Int, before: Array[Task], after: Array[Task]): (Array[Task], Array[Task]) = {
		var timeLeftVar = timeLeft
		val (toDoBefore, leftAfter) = after.sortBy(t => (-t.time.toDouble / t.earlinessCost, t.earlinessCost, -t.time, -t.tardinessCost))
		 .partition(t => {
			 if (timeLeftVar - t.time >= 0) {
				 timeLeftVar -= t.time
				 true
			 } else {
				 false
			 }
		 })
		moveTasks(before ++ toDoBefore, leftAfter)
	}

	private def moveTasks(toDoBefore: Array[Task], toDoAfter: Array[Task]): (Array[Task], Array[Task]) = {
		val sortedToDoBefore = toDoBefore.sortInPlaceBy(t => (-t.time.toDouble / t.earlinessCost, t.earlinessCost, -t.time, -t.tardinessCost)).toArray
		val sortedToDoAfter = toDoAfter.sortInPlaceBy(t => (-t.time.toDouble / t.tardinessCost, t.tardinessCost, -t.time, -t.earlinessCost)).reverse.toArray
		(sortedToDoBefore, sortedToDoAfter)
	}

	override def canMutate(): Boolean = !wasUsed
}
