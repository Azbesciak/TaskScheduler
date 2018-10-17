package cs.put.ptsz.taskscheduler

import java.io.{File, FileInputStream}
import java.util.Scanner

import cs.put.ptsz.taskscheduler.Control.using

object ProblemParser {
	def load(file: File): Array[Problem] = {
		using(new Scanner(new FileInputStream(file)))(getProblems)
	}

	private def getProblems(scanner: Scanner): Array[Problem] = {
		val problemsCount = scanner.nextInt()
		val problems = new Array[Problem](problemsCount)
		for (i <- 0 until problemsCount) {
			problems(i) = createProblem(scanner)
		}
		problems
	}

	private def createProblem(scanner: Scanner): Problem = {
		val tasksCount = scanner.nextInt()
		val tasks = new Array[Task](tasksCount)
		for (taskId <- 0 until tasksCount) {
			tasks(taskId) = Task(taskId, scanner.nextInt(), scanner.nextInt(), scanner.nextInt())
		}
		Problem(tasks)
	}
}