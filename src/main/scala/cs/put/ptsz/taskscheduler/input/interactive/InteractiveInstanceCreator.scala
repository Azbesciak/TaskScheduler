package cs.put.ptsz.taskscheduler.input.interactive

import cs.put.ptsz.taskscheduler.Util.executeTillValid
import cs.put.ptsz.taskscheduler.solver.{Instance, Problem}

import scala.io.StdIn

class InteractiveInstanceCreator(problems: Array[Problem]) {
	def get(): Instance = {
		val problem = chooseProblem
		val h = chooseHValue
		Instance(problem, h)
	}

	private def chooseProblem: Problem = {
		println(problems.zipWithIndex.map { case (t, i) => s"$i: $t}" }.mkString("\n"))
		executeTillValid("pass problem index:", () => {
			val index = StdIn.readInt()
			if (index >= 0 && index < problems.length)
				Some(problems(index))
			else {
				println(s"invalid problem index $index, allowed [0, ${problems.length-1}]")
				None
			}
		})
	}

	private def chooseHValue: Double = {
		executeTillValid("pass h value:", () => {
			val h = StdIn.readDouble()
			if (h > 0 && h <= 1 )
				Some(h)
			else {
				println(s"invalid h value, allowed (0, 1]")
				None
			}
		})
	}
}