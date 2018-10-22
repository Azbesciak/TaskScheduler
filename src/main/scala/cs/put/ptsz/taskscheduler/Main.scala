package cs.put.ptsz.taskscheduler

import java.util.concurrent.TimeUnit

import cs.put.ptsz.taskscheduler.cost.OneMachineScheduleEndTimeCostFunction
import cs.put.ptsz.taskscheduler.input.InstanceFactory
import cs.put.ptsz.taskscheduler.output.OutputProducer
import cs.put.ptsz.taskscheduler.solver.SimpleTaskScheduler
import cs.put.ptsz.taskscheduler.stopcondition.{AllValidStopCondition, ImprovingSolutionsStopCondition, SolutionsCountStopCondition, TimeLimitStopCondition}

import scala.concurrent.duration.Duration

object Main extends App {
	private val instance = InstanceFactory.resolve(args)
	private val costFunction = new OneMachineScheduleEndTimeCostFunction(instance)
	private val stopCondition = new AllValidStopCondition(
		new SolutionsCountStopCondition(1000),
		new ImprovingSolutionsStopCondition(100),
		new TimeLimitStopCondition(Duration(5, TimeUnit.SECONDS))
	)
	private val scheduler = new SimpleTaskScheduler(instance, stopCondition, costFunction)
	private val solution = scheduler.schedule()
	private val output = OutputProducer.make(instance, solution)
	println(output)
}

