package cs.put.ptsz.taskscheduler.solver

trait TaskScheduler {
	def schedule(): EvaluatedSolution
}