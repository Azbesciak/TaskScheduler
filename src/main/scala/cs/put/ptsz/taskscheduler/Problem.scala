package cs.put.ptsz.taskscheduler

case class Task(time: Int, earlinessCost: Int, tardinessCost: Int)
case class Problem(tasks: Array[Task])
