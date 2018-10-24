package cs.put.ptsz.taskscheduler.input

import cs.put.ptsz.taskscheduler.input.cmd.CmdInstanceProvider
import cs.put.ptsz.taskscheduler.input.interactive.InteractiveInstanceProvider
import cs.put.ptsz.taskscheduler.solver.Instance


object InstanceFactory extends InstanceProvider {
	override def provide(args: Array[String]): Array[Instance] = {
		args.length match {
			case 1 | 0 => InteractiveInstanceProvider.provide(args)
			case 2 | 3 => CmdInstanceProvider.provide(args)
			case _ => throw new IllegalArgumentException("Invalid args count")
		}
	}
}
