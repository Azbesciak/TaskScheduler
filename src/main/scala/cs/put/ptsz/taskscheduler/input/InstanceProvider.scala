package cs.put.ptsz.taskscheduler.input

import cs.put.ptsz.taskscheduler.solver.Instance


trait InstanceProvider {
	def provide(args: Array[String]): Instance
}