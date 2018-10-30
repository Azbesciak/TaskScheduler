package cs.put.ptsz.taskscheduler.solver.mutator

import cs.put.ptsz.taskscheduler.solver.{Instance, Task}

class CenterOrientedTasksMutator(instance: Instance) extends TasksMutator {
	private var wasUsed = false

	override def mutate(tasks: Array[Task]): Array[Task] = {
		wasUsed = true
		val toMakeBefore = tasks.sortBy(t => (-t.earlinessCost, t.time)).zipWithIndex.sortBy(_._1.id)
		val toMakeAfter = tasks.sortBy(t => (-t.tardinessCost, t.time)).zipWithIndex.sortBy(_._1.id)
		var timeLeftBeforeDueTime = instance.dueTime
		toMakeBefore zip toMakeAfter map {
			case (t1, t2) =>
				if (t1._2 > t2._2 && timeLeftBeforeDueTime - t1._1.time > 0) {
					timeLeftBeforeDueTime -= t1._1.time
					(t1._1, -t1._2)
				} else t2
		} sortBy (_._2) map (_._1)
	}

	override def canMutate(): Boolean = !wasUsed
}
