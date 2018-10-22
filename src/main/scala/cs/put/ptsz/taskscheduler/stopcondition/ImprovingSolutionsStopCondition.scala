package cs.put.ptsz.taskscheduler.stopcondition

import cs.put.ptsz.taskscheduler.solver.EvaluatedSolution

class ImprovingSolutionsStopCondition(private val notBetterSolutionsLimit: Int) extends StopCondition {
	private var best: EvaluatedSolution = _
	private var notBetterSolutions: Int = _

	override def initialize(): Unit = {
		best = null
		notBetterSolutions = 0
	}

	override def canContinue(solution: EvaluatedSolution): Boolean = {
		if (best == null || solution.cost < best.cost) {
			best = solution
			notBetterSolutions = 0
		} else {
			notBetterSolutions += 1
		}
		notBetterSolutions < notBetterSolutionsLimit
	}
}
