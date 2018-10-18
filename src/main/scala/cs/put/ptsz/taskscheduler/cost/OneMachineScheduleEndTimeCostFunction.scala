package cs.put.ptsz.taskscheduler.cost

import cs.put.ptsz.taskscheduler.solver.{Instance, Solution}


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