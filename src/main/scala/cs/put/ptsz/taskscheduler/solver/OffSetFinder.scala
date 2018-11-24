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
		val middlePoint = findMiddle(start, end)

		if (start.cost < end.cost) {
			find(start, middlePoint)
		} else {
			find(middlePoint, end)
		}
	}

	private def findMiddle(start: EvaluatedSolution, end: EvaluatedSolution) = {
		val sum = start.offset + end.offset
		val middle = sum / 2
		val lowerCenter = getForOffset(middle)
		sum % 2 match {
			case 0 => lowerCenter
			case 1 =>
				val upperCenter = getForOffset(middle + 1)
				if (lowerCenter.cost < upperCenter.cost) lowerCenter else upperCenter
		}
	}

	private def getForOffset(offset: Int): EvaluatedSolution = {
		val solution = Solution(TaskOffsetAssigner.assign(tasks, offset))
		val cost = costFunction.cost(solution)
		EvaluatedSolution(solution, cost, offset)
	}
}