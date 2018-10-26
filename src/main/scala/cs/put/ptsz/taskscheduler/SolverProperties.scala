package cs.put.ptsz.taskscheduler

import scala.util.Properties

object SolverProperties {
	def get() = SolverProperties(
		Properties.propOrElse("stopCondition.maxSolutions", "100").toInt,
		Properties.propOrElse("stopCondition.notImprovingSolutions", "10").toInt,
		Properties.propOrElse("stopCondition.timeLimit", "1s")
	)
}

case class SolverProperties(
	maxSolutions: Int,
	notImprovingSolutionsLimit: Int,
	timeLimit: String
)
