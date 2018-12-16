package cs.put.ptsz.taskscheduler.solver.mutator

import cs.put.ptsz.taskscheduler.solver.mutator.TasksSorter._
import cs.put.ptsz.taskscheduler.solver.{Instance, Task}

class PartitioningTaskMutator(val instance: Instance, private val partitioner: Partitioner) extends TasksMutator {
	private[this] val splitStrategies = Array(
		AllBeforeStrategy,
		AllAfterStrategy,
		new EqualNumberStrategy(0),
		new EqualNumberStrategy(1)
	).iterator

	override def mutate(tasks: Array[Task]): Array[Task] = {
		require(splitStrategies.hasNext, "no other combinations of splitting equal tasks")
		val PartitionedTasksScheduling(toDoBefore, equal, toDoAfter) = partitioner.tasks
		val (beq, aeq) = splitStrategies.next().split(equal)
		val (sortedToDoBefore, sortedToDoAfter) = sortTasks(toDoBefore ++ beq, toDoAfter ++ aeq)
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
		sortTasks(leftBefore, toDoAfter ++ after)
	}

	override def canMutate(): Boolean = splitStrategies.hasNext
}

trait EqualSplitStrategy {
	def split(tasks: Array[Task]): (Array[Task], Array[Task])
}

object AllBeforeStrategy extends EqualSplitStrategy {
	override def split(tasks: Array[Task]): (Array[Task], Array[Task]) = (tasks, Array.empty)
}

object AllAfterStrategy extends EqualSplitStrategy {
	override def split(tasks: Array[Task]): (Array[Task], Array[Task]) = (Array.empty, tasks)
}

class EqualNumberStrategy(private val initValue: Int) extends EqualSplitStrategy {
	override def split(tasks: Array[Task]): (Array[Task], Array[Task]) = {
		var i = initValue
		tasks.sortBy(_.earlinessCost).partition(_ => {
			i += 1
			i % 2 == 0
		})
	}
}

