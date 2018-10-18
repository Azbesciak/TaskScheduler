package cs.put.ptsz.taskscheduler

case class Task(id: Int, time: Int, earlinessCost: Int, tardinessCost: Int)

case class Problem(tasks: Array[Task]) {
	override def toString: String = s"Problem(${tasks.mkString("[", ",", "]")})"
}

case class Instance(problem: Problem, h: Double) {
	val summaryTime: Int = problem.tasks.map(_.time).sum
}

case class Solution(tasks: Array[TaskSchedule]) {
	override def toString: String = s"Solution${tasks.mkString("[", ",", "]")}"
}

case class TaskSchedule(task: Task, start: Int) {
	val end: Int = start + task.time
}