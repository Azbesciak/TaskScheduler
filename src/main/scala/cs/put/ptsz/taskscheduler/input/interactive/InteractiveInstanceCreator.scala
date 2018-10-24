package cs.put.ptsz.taskscheduler.input.interactive

import cs.put.ptsz.taskscheduler.Util.executeTillValid
import cs.put.ptsz.taskscheduler.solver.{Instance, Problem}

import scala.io.StdIn

class InteractiveInstanceCreator(problems: Array[Problem]) {
	def get(): Array[Instance] = {
		val problem = chooseProblem
		val h = chooseHValue
		problem.map(Instance(_, h))
	}

	private def chooseProblem: Array[Problem] = {
		println(problems.zipWithIndex.map { case (t, i) => s"$i: $t}" }.mkString("\n"))
		executeTillValid("pass problem index (-1 == all):", () => {
			val index = StdIn.readInt()
			if (index == -1) {
				Some(problems)
			} else if (index >= 0 && index < problems.length)
				Some(Array(problems(index)))
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