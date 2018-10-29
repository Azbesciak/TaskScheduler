package cs.put.ptsz.taskscheduler.solver

import cs.put.ptsz.taskscheduler.cost.CostFunction

import scala.annotation.tailrec

class OffSetFinder(
	private val costFunction: CostFunction,
	private val tasks: Array[Task]
) {
	@tailrec final def find(
		start: EvaluatedSolution = getForOffset(0),
		end: EvaluatedSolution = getForOffset(tasks.map(_.time).sum)
	): EvaluatedSolution = {
		if (end.offset - start.offset <= 1) {
			return if (start.cost < end.cost) start else end
		}
		val middlePoint = (start.offset + end.offset) / 2
		if (start.cost < end.cost) {
			find(start, getForOffset(middlePoint))
		} else {
			find(getForOffset(middlePoint), end)
		}
	}

	private def getForOffset(offset: Int): EvaluatedSolution = {
		val solution = Solution(TaskOffsetAssigner.assign(tasks, offset))
		val cost = costFunction.cost(solution)
		EvaluatedSolution(solution, cost, offset)
	}
}