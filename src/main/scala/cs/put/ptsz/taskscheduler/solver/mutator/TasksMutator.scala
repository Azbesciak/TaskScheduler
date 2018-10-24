package cs.put.ptsz.taskscheduler.solver.mutator

import cs.put.ptsz.taskscheduler.solver.Task


trait TasksMutator {
	/**
		* Changes order of tasks for further optimization. Does not change given tasks.
		* If mutator finished
		* @param tasks tasks to shuffle
		* @return tasks with changed order
		*/
	def mutate(tasks: Array[Task]): Array[Task]
	def canMutate(): Boolean
}