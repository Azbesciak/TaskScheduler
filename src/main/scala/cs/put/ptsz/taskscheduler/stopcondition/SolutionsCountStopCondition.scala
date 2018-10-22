package cs.put.ptsz.taskscheduler.stopcondition

import cs.put.ptsz.taskscheduler.solver.EvaluatedSolution

class SolutionsCountStopCondition(private val limit: Int) extends StopCondition {
	private var solutionsCount = 0

	override def canContinue(solution: EvaluatedSolution): Boolean = {
		solutionsCount += 1
		solutionsCount < limit
	}
}

