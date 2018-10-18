package cs.put.ptsz.taskscheduler

import scala.annotation.tailrec

trait StopCondition {
	def canContinue(solution: EvaluatedSolution): Boolean
}

class SolutionsCountStopCondition(
	private val limit: Int
) extends StopCondition {
	private var solutionsCount = 0

	override def canContinue(solution: EvaluatedSolution): Boolean = {
		solutionsCount += 1
		solutionsCount < limit
	}
}

trait CostFunction {
	def cost(solution: Solution): Cost
}

class OneMachineScheduleEndTimeCostFunction(
	instance: Instance,
) extends CostFunction {
	private val maxTime = instance.summaryTime
	private val dueTime = math.floor(maxTime * instance.h).toInt

	override def cost(solution: Solution): Cost = {
		val cost = solution.tasks.map(t => {
			val delay = t.end - dueTime
			if (delay > 0)
				delay * t.task.tardinessCost
			else
				-delay * t.task.earlinessCost
		}).sum
		Cost(cost)
	}
}

case class Cost(
	value: Int
) {
	def <(cost: Cost): Boolean = {
		value < cost.value
	}
}

case class EvaluatedSolution(
	solution: Solution,
	cost: Cost,
	offset: Int
)

class Assigner(
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
		val solution = Solution(assign(tasks, offset))
		val cost = costFunction.cost(solution)
		EvaluatedSolution(solution, cost, offset)
	}

	private def assign(tasks: Array[Task], startOffset: Int): Array[TaskSchedule] = {
		var lastEndTime = startOffset
		tasks.map(t => {
			val startTime = lastEndTime
			lastEndTime += t.time
			TaskSchedule(t, startTime)
		})
	}
}

class TaskScheduler(
	private val instance: Instance,
	private val stopCondition: StopCondition,
	private val costFunction: CostFunction
) {
	private val tasks: Array[Task] = instance.problem.tasks

	def schedule(): EvaluatedSolution = {
		val assigner = new Assigner(costFunction, tasks)
		assigner.find()
	}
}
