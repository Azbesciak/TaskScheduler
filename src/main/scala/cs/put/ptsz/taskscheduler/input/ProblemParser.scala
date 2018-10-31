package cs.put.ptsz.taskscheduler.input

import java.io.{File, FileInputStream}
import java.util.Scanner

import cs.put.ptsz.taskscheduler.Util.using
import cs.put.ptsz.taskscheduler.solver.{Problem, Task}

object ProblemParser {
	def load(file: File): Array[Problem] = {
		using(new Scanner(new FileInputStream(file)))(getProblems)
	}

	private def getProblems(scanner: Scanner): Array[Problem] = {
		val problemsCount = scanner.nextInt()
		val problems = new Array[Problem](problemsCount)
		for (i <- 0 until problemsCount) {
			problems(i) = createProblem(scanner, i)
		}
		problems
	}

	private def createProblem(scanner: Scanner, index: Int): Problem = {
		val tasksCount = scanner.nextInt()
		val tasks = new Array[Task](tasksCount)
		for (taskId <- 0 until tasksCount) {
			tasks(taskId) = Task(taskId, scanner.nextInt(), scanner.nextInt(), scanner.nextInt())
		}
		Problem(index + 1, tasks)
	}
}
