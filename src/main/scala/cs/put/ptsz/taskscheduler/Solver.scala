package cs.put.ptsz.taskscheduler

import cs.put.ptsz.taskscheduler.Util.measureTime
import cs.put.ptsz.taskscheduler.cost.OneMachineScheduleEndTimeCostFunction
import cs.put.ptsz.taskscheduler.input.InstanceFactory
import cs.put.ptsz.taskscheduler.output.OutputProducer
import cs.put.ptsz.taskscheduler.solver.mutator._
import cs.put.ptsz.taskscheduler.solver.{Result, SimpleTaskScheduler}
import cs.put.ptsz.taskscheduler.stopcondition.{AllValidStopCondition, ImprovingSolutionsStopCondition, SolutionsCountStopCondition, TimeLimitStopCondition}

import scala.concurrent.duration.Duration

object Solver extends App {
	private val props = SolverProperties.get()
	val results = InstanceFactory.provide(args)
	 .map(instance => {
		 val costFunction = new OneMachineScheduleEndTimeCostFunction(instance)
		 val stopCondition = new AllValidStopCondition(
			 new SolutionsCountStopCondition(props.maxSolutions),
			 new ImprovingSolutionsStopCondition(props.notImprovingSolutionsLimit),
			 new TimeLimitStopCondition(Duration(props.timeLimit))
		 )
		 val mutator = new AggregatedTaskMutator(
			 new SameSingleTimeTaskMutator,
			 new CenterOrientedTasksMutator(instance),
			 new WeightedSortTasksMutator,
			 new SortTaskMutator(instance)
		 )
		 val scheduler = new SimpleTaskScheduler(instance, stopCondition, costFunction, mutator)
		 val (solution, duration) = measureTime {
			 scheduler.schedule()
		 }
		 Result(instance, solution, duration)
	 })
	OutputProducer.consume(results)
}

