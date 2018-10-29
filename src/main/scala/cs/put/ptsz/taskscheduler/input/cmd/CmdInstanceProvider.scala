package cs.put.ptsz.taskscheduler.input.cmd

import java.io.File

import cs.put.ptsz.taskscheduler.Util.mapToIndexes
import cs.put.ptsz.taskscheduler.input.{InstanceProvider, ProblemParser}
import cs.put.ptsz.taskscheduler.solver.Instance


object CmdInstanceProvider extends InstanceProvider {
	override def provide(args: Array[String]): Array[Instance] = {
		require(args.length == 3 || args.length == 2, () => "usage : <path to problem file> [problem id] <h value>")
		val instanceFile = new File(args(0))
		require(instanceFile.isFile, () => "given file is not a file")
		val problems = ProblemParser.load(instanceFile)
		val hValue = args.last.toDouble
		val chosenProblems = args.length match {
			case 3 => mapToIndexes(args(1)).map(problems(_))
			case _ => problems
		}
		chosenProblems.map(Instance(_, hValue))
	}
}