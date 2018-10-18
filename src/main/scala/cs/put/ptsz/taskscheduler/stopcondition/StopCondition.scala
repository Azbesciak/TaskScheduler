package cs.put.ptsz.taskscheduler.stopcondition

import cs.put.ptsz.taskscheduler.solver.EvaluatedSolution


trait StopCondition {
	def canContinue(solution: EvaluatedSolution): Boolean
}
