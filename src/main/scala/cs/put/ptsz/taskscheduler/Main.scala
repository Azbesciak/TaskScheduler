package cs.put.ptsz.taskscheduler

object Main extends App {
	private val provider = new ProblemsProvider("src/main/resources")
	provider.list()
	private val file = provider.getFile
	private val problems = ProblemParser.load(file)
	private val instanceCreator = new InstanceCreator(problems)
	private val instance = instanceCreator.get()
	private val costFunction = new OneMachineScheduleEndTimeCostFunction(instance)
	private val stopCondition = new SolutionsCountStopCondition(1)
	private val scheduler = new TaskScheduler(instance, stopCondition, costFunction)
	private val solution = scheduler.schedule()
	println(solution)
}

