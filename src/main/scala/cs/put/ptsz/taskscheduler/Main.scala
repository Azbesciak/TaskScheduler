package cs.put.ptsz.taskscheduler

import cs.put.ptsz.taskscheduler.cost.OneMachineScheduleEndTimeCostFunction
import cs.put.ptsz.taskscheduler.input.InstanceFactory
import cs.put.ptsz.taskscheduler.solver.SimpleTaskScheduler
import cs.put.ptsz.taskscheduler.stopcondition.SolutionsCountStopCondition

object Main extends App {
	private val instance = InstanceFactory.resolve(args)
	private val costFunction = new OneMachineScheduleEndTimeCostFunction(instance)
	private val stopCondition = new SolutionsCountStopCondition(1)
	private val scheduler = new SimpleTaskScheduler(instance, stopCondition, costFunction)
	private val solution = scheduler.schedule()
	println(solution)
}

