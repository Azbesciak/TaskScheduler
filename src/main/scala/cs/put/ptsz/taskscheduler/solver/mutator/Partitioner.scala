package cs.put.ptsz.taskscheduler.solver.mutator

import cs.put.ptsz.taskscheduler.solver.Task

class Partitioner(unorderedTasks: Array[Task]) {
	val tasks: PartitionedTasksScheduling = {
		val (before, afterAndEqual) = unorderedTasks.partition(t => t.earlinessCost < t.tardinessCost)
		val (equal, after) = afterAndEqual.partition(t => t.tardinessCost == t.earlinessCost)
		PartitionedTasksScheduling(before, equal, after)
	}
}

case class PartitionedTasksScheduling(
	before: Array[Task],
	equal: Array[Task],
	after: Array[Task]
)
