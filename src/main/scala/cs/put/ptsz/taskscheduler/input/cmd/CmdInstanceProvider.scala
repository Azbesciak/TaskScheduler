package cs.put.ptsz.taskscheduler.input.cmd

import java.io.File

import cs.put.ptsz.taskscheduler.input.{InstanceProvider, ProblemParser}
import cs.put.ptsz.taskscheduler.solver.Instance


object CmdInstanceProvider extends InstanceProvider {
	override def provide(args: Array[String]): Instance = {
		require(args.length == 3, () => "usage : <path to problem file> <problem id> <h value>")
		val instanceFile = new File(args(0))
		require(instanceFile.isFile, () => "given file is not a file")
		val problems = ProblemParser.load(instanceFile)
		val problemIndex = args(1).toInt
		val hValue = args(2).toDouble
		Instance(problems(problemIndex), hValue)
	}
}