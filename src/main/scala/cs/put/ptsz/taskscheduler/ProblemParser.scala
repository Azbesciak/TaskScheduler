package cs.put.ptsz.taskscheduler

import java.io.{File, FileInputStream}
import java.util.Scanner

import jdk.nashorn.internal.runtime.regexp.joni.Regex

import Control.using

object ProblemParser {
	private val regex = new Regex("\\s+")

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
			tasks(taskId) = Task(scanner.nextInt(), scanner.nextInt(), scanner.nextInt())
		}
		Problem(tasks)
	}
}