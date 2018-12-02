package cs.put.ptsz.taskscheduler

import cs.put.ptsz.taskscheduler.Solver.solve
import cs.put.ptsz.taskscheduler.Util.measureTime
import cs.put.ptsz.taskscheduler.cost.OneMachineScheduleEndTimeCostFunction
import cs.put.ptsz.taskscheduler.input.InstanceFactory
import cs.put.ptsz.taskscheduler.output.OutputProducer
import cs.put.ptsz.taskscheduler.solver.mutator._
import cs.put.ptsz.taskscheduler.solver._
import cs.put.ptsz.taskscheduler.stopcondition.{AllValidStopCondition, ImprovingSolutionsStopCondition, SolutionsCountStopCondition, TimeLimitStopCondition}

import scala.concurrent.duration.Duration
import scala.util.Properties

object Solver {
	def solve(instance: Instance, props: SolverProperties): Result = {
		val costFunction = new OneMachineScheduleEndTimeCostFunction(instance)
		val stopCondition = new AllValidStopCondition(
			new SolutionsCountStopCondition(props.maxSolutions),
			new ImprovingSolutionsStopCondition(props.notImprovingSolutionsLimit),
			new TimeLimitStopCondition(Duration(props.timeLimit))
		)
		val partitioner = new Partitioner(instance.problem.tasks)
		val mutator = new AggregatedTaskMutator(
			new PartitioningTaskMutator(instance, partitioner),
			new CenterOrientedTasksMutator(instance),
			new SortTaskMutator(instance),
			new DueDateTaskMutator(instance, t => new OffSetFinder(costFunction, t).find()),
			new EqualTimeSwapperTaskMutator(partitioner),
		)
		val scheduler = new SimpleTaskScheduler(instance, stopCondition, costFunction, mutator)
		val (solution, duration) = measureTime {
			scheduler.schedule()
		}
		Result(instance, solution, duration)
	}
}

object Runner extends App {
	private val props = SolverProperties.get()
	val results = InstanceFactory.provide(args)
	 .map(instance => solve(instance, props))
	OutputProducer.consume(results)
}

object Benchmark extends App {
	private val props = SolverProperties.get()
	val retries = Properties.propOrElse("benchmark", "25").toInt
	OutputProducer.enable(OutputProducer.MEASURE_TIME_PROP)
	OutputProducer.enable(OutputProducer.DETAILS_PROP)
	val results = InstanceFactory.provide(args)
	 .map(instance => {
		 (0 until retries).map { _ =>
			 solve(instance, props)
		 }.sortBy(_.duration).toArray.apply(retries / 2)
	 })
	OutputProducer.consume(results)
}

