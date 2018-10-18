package cs.put.ptsz.taskscheduler.cost

import cs.put.ptsz.taskscheduler.solver.Solution


trait CostFunction {
	def cost(solution: Solution): Cost
}