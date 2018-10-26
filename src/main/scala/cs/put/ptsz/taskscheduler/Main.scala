package cs.put.ptsz.taskscheduler

import java.util.concurrent.TimeUnit

import cs.put.ptsz.taskscheduler.cost.OneMachineScheduleEndTimeCostFunction
import cs.put.ptsz.taskscheduler.input.InstanceFactory
import cs.put.ptsz.taskscheduler.output.OutputProducer
import cs.put.ptsz.taskscheduler.solver.SimpleTaskScheduler
import cs.put.ptsz.taskscheduler.solver.mutator.{AggregatedTaskMutator, SameSingleTimeTaskMutator, SortTaskMutator}
import cs.put.ptsz.taskscheduler.stopcondition.{AllValidStopCondition, ImprovingSolutionsStopCondition, SolutionsCountStopCondition, TimeLimitStopCondition}

import scala.concurrent.duration.Duration

object Main extends App {
	InstanceFactory.provide(args)
	 .map(instance => {
		 val costFunction = new OneMachineScheduleEndTimeCostFunction(instance)
		 val stopCondition = new AllValidStopCondition(
			 new SolutionsCountStopCondition(1000),
			 new ImprovingSolutionsStopCondition(100),
			 new TimeLimitStopCondition(Duration(5, TimeUnit.SECONDS))
		 )
		 val mutator = new AggregatedTaskMutator(
			 new SameSingleTimeTaskMutator,
			 new SortTaskMutator(instance)
		 )
		 val scheduler = new SimpleTaskScheduler(instance, stopCondition, costFunction, mutator)
		 val solution = scheduler.schedule()
		 OutputProducer.make(instance, solution)
	 }).foreach(println)
}

