package cs.put.ptsz.taskscheduler.solver.mutator

import cs.put.ptsz.taskscheduler.solver.Task

import scala.util.Random


class RandomTasksMutator(seed: Int) extends TasksMutator {
	private val random = new Random(seed)
	override def mutate(tasks: Array[Task]): Array[Task] = random.shuffle(tasks.toSeq).toArray

	override def canMutate(): Boolean = true
}