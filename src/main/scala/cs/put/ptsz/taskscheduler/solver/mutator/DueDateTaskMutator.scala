package cs.put.ptsz.taskscheduler.solver.mutator

import cs.put.ptsz.taskscheduler.solver.{EvaluatedSolution, Instance, Task}

import scala.collection.mutable.ArrayBuffer
import scala.collection.parallel.immutable.ParSeq

class DueDateTaskMutator(
	private val instance: Instance,
	private val solutionEvaluator: Array[Task] => EvaluatedSolution
) extends TasksMutator {
	private val swappers = Array(
		BeforeToAfterTaskSwapper,
		AfterToBeforeTaskSwapper,
		AfterToBeforeForceSwapper(solutionEvaluator),
		BeforeToAfterForceSwapper(solutionEvaluator),
		ExchangeForceSwapper(solutionEvaluator, 0, 3),
		BeforeToAfterForceSwapper(solutionEvaluator),
		AfterToBeforeForceSwapper(solutionEvaluator),
		ExchangeForceSwapper(solutionEvaluator, 1, 3),
		null
	).iterator
	private var currentSwapper: TaskSwapper = NoOpTaskSwapper

	override def mutate(tasks: Array[Task]): Array[Task] = {
		require(currentSwapper != null, "no more swappers!")
		val oldSolution = solutionEvaluator(tasks)
		val indexOfCenterTask = oldSolution.solution.tasks.indexWhere(_.start > instance.dueTime)
		val (bef, aft) = if (indexOfCenterTask >= 0)
			tasks.splitAt(indexOfCenterTask)
		else
			(tasks, Array.empty[Task])
		val (bestBefore, bestAfter) = currentSwapper.swap(bef, aft)
		val newSolutionTasks = TasksSorter.sortByEarlinessCost(bestBefore) ++ TasksSorter.sortByTardinessCost(bestAfter).reverse
		val newSolution = solutionEvaluator(newSolutionTasks)
		if (oldSolution.cost.value <= newSolution.cost.value) {
			currentSwapper = swappers.next()
		}
		newSolutionTasks
	}

	override def canMutate(): Boolean = currentSwapper != null
}

trait TaskSwapper {
	def swap(bef: Array[Task], after: Array[Task]): (Array[Task], Array[Task])
}

object NoOpTaskSwapper extends TaskSwapper {
	override def swap(bef: Array[Task], after: Array[Task]): (Array[Task], Array[Task]) = (bef, after)
}

object BeforeToAfterTaskSwapper extends TaskSwapper {
	override def swap(bef: Array[Task], after: Array[Task]): (Array[Task], Array[Task]) = {
		if (bef.isEmpty)
			return (bef, after)
		val sortedByTardiness = TasksSorter.sortByTardinessCost(bef)
		(sortedByTardiness.drop(1), after :+ sortedByTardiness.head)
	}
}

object AfterToBeforeTaskSwapper extends TaskSwapper {
	override def swap(bef: Array[Task], after: Array[Task]): (Array[Task], Array[Task]) = {
		if (after.isEmpty)
			return (bef, after)
		val sortedByEarliness = TasksSorter.sortByEarlinessCost(after)
		(bef :+ sortedByEarliness.head, sortedByEarliness.drop(1))
	}
}

case class ExchangeForceSwapper(solutionEvaluator: Array[Task] => EvaluatedSolution, start: Int, count: Int = 1)
 extends ForceBestSwapper(solutionEvaluator) {
	private val bta = BeforeToAfterForceSwapper(solutionEvaluator)
	private val atb = AfterToBeforeForceSwapper(solutionEvaluator)
	private val maxPopulationSize = 5
	private val swappers = (start until (start + count)).map(v =>
		if (v % 2 == 0) (bef: Array[Task], to: Array[Task]) => bta.evaluateWithMoved(bef, to)
		else (bef: Array[Task], to: Array[Task]) => atb.evaluateWithMoved(to, bef)
	).toArray

	override def swap(bef: Array[Task], after: Array[Task]): (Array[Task], Array[Task]) = {
		val bestBuffer = ArrayBuffer[(Array[Task], Array[Task], EvaluatedSolution)]()
		swappers.foldLeft(Array((bef, after, solutionEvaluator(bef ++ after)))) {
			case (arr, swapper) =>
				limit(arr.flatMap(t => {
					val res = limit(swapper(t._1, t._2).toArray)
					bestBuffer.appendAll(res)
					res
				}))
		}
		val (f, t, b) = bestBuffer.minBy(_._3.cost.value)
		(f, t)
	}

	private def limit(tasks: Array[(Array[Task], Array[Task], EvaluatedSolution)]) = {
		val sorted = tasks.sortBy(_._3.cost.value)
		if (tasks.length > maxPopulationSize) {
			sorted.take(maxPopulationSize)
		} else sorted
	}

	override protected def prepareInOrder(from: Array[Task], to: Array[Task]): (Array[Task], Array[Task]) = (from, to)
}

case class BeforeToAfterForceSwapper(solutionEvaluator: Array[Task] => EvaluatedSolution)
 extends ForceBestSwapper(solutionEvaluator) {
	override def swap(bef: Array[Task], after: Array[Task]): (Array[Task], Array[Task]) =
		findBest(bef, after)

	override protected def prepareInOrder(from: Array[Task], to: Array[Task]): (Array[Task], Array[Task]) =
		(from, TasksSorter.sortByTardinessCost(to).reverse)
}

case class AfterToBeforeForceSwapper(solutionEvaluator: Array[Task] => EvaluatedSolution)
 extends ForceBestSwapper(solutionEvaluator) {
	override def swap(bef: Array[Task], after: Array[Task]): (Array[Task], Array[Task]) =
		findBest(after, bef)

	override protected def prepareInOrder(from: Array[Task], to: Array[Task]): (Array[Task], Array[Task]) =
		(TasksSorter.sortByEarlinessCost(to), from)
}

abstract class ForceBestSwapper(private val evaluator: Array[Task] => EvaluatedSolution) extends TaskSwapper {
	protected def prepareInOrder(from: Array[Task], to: Array[Task]): (Array[Task], Array[Task])

	protected def findBest(from: Array[Task], to: Array[Task]): (Array[Task], Array[Task]) = {
		val (bestBefore, bestAfter, _) = evaluateWithMoved(from, to).minBy(_._3.cost.value)
		(bestBefore, bestAfter)
	}

	def evaluateWithMoved(from: Array[Task], to: Array[Task]):
	ParSeq[(Array[Task], Array[Task], EvaluatedSolution)] =
		moveTask(from, to)
		 .map { case (a, b) =>
			 val (sortedBefore, sortedAfter) = prepareInOrder(a, b)
			 (sortedBefore, sortedAfter, evaluator(sortedBefore ++ sortedAfter))
		 }

	private def moveTask(from: Array[Task], to: Array[Task]): ParSeq[(Array[Task], Array[Task])] = {
		from.toStream.par.zipWithIndex.map { case (t, i) =>
			val withoutThis = from.toBuffer
			withoutThis.remove(i)
			val toWith = to :+ t
			(withoutThis.toArray, toWith)
		}
	}
}