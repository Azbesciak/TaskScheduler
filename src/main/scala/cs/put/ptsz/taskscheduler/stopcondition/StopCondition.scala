package cs.put.ptsz.taskscheduler.stopcondition

import cs.put.ptsz.taskscheduler.solver.EvaluatedSolution


trait StopCondition {
	def initialize() = {}
	def canContinue(solution: EvaluatedSolution): Boolean
}
