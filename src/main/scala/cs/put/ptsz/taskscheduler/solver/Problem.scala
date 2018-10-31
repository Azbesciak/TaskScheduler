package cs.put.ptsz.taskscheduler.solver

import cs.put.ptsz.taskscheduler.cost.Cost

import scala.concurrent.duration.Duration

case class Task(id: Int, time: Int, earlinessCost: Int, tardinessCost: Int)

case class Problem(id: Int, tasks: Array[Task]) {
	override def toString: String = s"Problem($id, ${tasks.mkString("[", ",", "]")})"
}

case class Instance(problem: Problem, h: Double) {
	private val summaryTime = problem.tasks.map(_.time).sum
	val dueTime: Int = math.floor(summaryTime * h).toInt
}

case class Solution(tasks: Array[TaskSchedule]) {
	override def toString: String = s"Solution${tasks.mkString("[", ",", "]")}"
}

case class EvaluatedSolution(
	solution: Solution,
	cost: Cost,
	offset: Int
)

case class TaskSchedule(task: Task, start: Int) {
	val end: Int = start + task.time
}

case class Result(
	instance: Instance,
	solution: EvaluatedSolution,
	duration: Duration
)