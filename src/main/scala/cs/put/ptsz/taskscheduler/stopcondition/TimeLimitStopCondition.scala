package cs.put.ptsz.taskscheduler.stopcondition

import java.time.Instant

import cs.put.ptsz.taskscheduler.solver.EvaluatedSolution

import scala.concurrent.duration.Duration

class TimeLimitStopCondition(duration: Duration) extends StopCondition {
	private var startTime: Long = _
	private val allowedDuration = duration.toMillis

	override def initialize(): Unit = {
		startTime = System.currentTimeMillis()
	}

	override def canContinue(solution: EvaluatedSolution): Boolean =
		System.currentTimeMillis() - startTime < allowedDuration
}
