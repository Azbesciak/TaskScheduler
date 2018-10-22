package cs.put.ptsz.taskscheduler.stopcondition

import cs.put.ptsz.taskscheduler.solver.EvaluatedSolution

class AllValidStopCondition(
	private val stopConditions: StopCondition*
) extends StopCondition {
	override def initialize(): Unit = {
		stopConditions.foreach(_.initialize())
	}

	override def canContinue(solution: EvaluatedSolution): Boolean =
		stopConditions.forall(_.canContinue(solution))
}
