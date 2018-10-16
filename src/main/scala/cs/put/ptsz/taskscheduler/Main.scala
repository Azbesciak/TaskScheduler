package cs.put.ptsz.taskscheduler

object Main extends App {
	private val provider = new ProblemsProvider("src/main/resources")
	provider.list()
	private val file = provider.getFile
	private val problems = ProblemParser.load(file)
	private val instanceCreator = new InstanceCreator(problems)
	private val instance = instanceCreator.get()
	println(s"instance: $instance")
}

