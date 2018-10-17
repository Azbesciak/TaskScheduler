package cs.put.ptsz.taskscheduler

case class Task(id: Int, time: Int, earlinessCost: Int, tardinessCost: Int)
case class Problem(tasks: Array[Task]) {
	override def toString: String = s"Problem(${tasks.mkString("[", ",", "]")})"
}
case class Instance(problem: Problem, h: Double)
case class Solution(tasks: Array[Task])