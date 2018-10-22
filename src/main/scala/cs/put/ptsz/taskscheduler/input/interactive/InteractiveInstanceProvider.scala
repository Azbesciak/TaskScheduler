package cs.put.ptsz.taskscheduler.input.interactive

import cs.put.ptsz.taskscheduler.input.{InstanceProvider, ProblemParser}
import cs.put.ptsz.taskscheduler.solver.Instance


object InteractiveInstanceProvider extends InstanceProvider {
	private val DEFAULT_SOURCE = "src/main/resources"

	override def provide(args: Array[String]): Instance = {
		val dirPath = if (args.length == 1) args(0) else DEFAULT_SOURCE
		val provider = new InteractiveProblemsProvider(dirPath)
		provider.list()
		val file = provider.getFile
		val problems = ProblemParser.load(file)
		val instanceCreator = new InteractiveInstanceCreator(problems)
		instanceCreator.get()
	}
}