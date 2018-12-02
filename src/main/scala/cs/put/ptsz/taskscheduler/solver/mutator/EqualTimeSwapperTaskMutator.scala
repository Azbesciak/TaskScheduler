package cs.put.ptsz.taskscheduler.solver.mutator

import cs.put.ptsz.taskscheduler.solver.Task

class EqualTimeSwapperTaskMutator(val partitioner: Partitioner) extends TasksMutator {
	private var beforeSet: Array[Task] = _
	private var afterSet: Array[Task] = _
	private var equalSet: Array[(Task, Int)] = _
	private var equalIterator: Iterator[BigInt] = _

	override def mutate(tasks: Array[Task]): Array[Task] = {
		if (equalIterator == null) {
			val PartitionedTasksScheduling(before, equal, after) = partitioner.tasks
			beforeSet = before
			equalSet = equal.zipWithIndex
			afterSet = after
			equalIterator = if (equal.length == 0) Iterator.empty else createSet(equal.length).iterator
			if (!equalIterator.hasNext) return tasks
		}
		require(equalIterator.hasNext, "cannot mutate - no more mutations")
		val split = equalIterator.next()
		val (be, ae) = equalSet.partition(t => split.testBit(t._2))
		val (b, e) = TasksSorter.sortTasks(beforeSet ++ be.map(_._1), afterSet ++ ae.map(_._1))
		b ++ e
	}

	def createSet(len: Int) = {
		val limit = BigInt("1".repeat(len))
		LazyList.from(0).map(BigInt(_)).takeWhile(v => v <= limit).iterator
	}

	override def canMutate(): Boolean = equalIterator == null || equalIterator.hasNext
}
