package cs.put.ptsz.taskscheduler

import cs.put.ptsz.taskscheduler.solver.{Instance, Problem}


object Util {
	def executeTillValid[T](request: String, f: () => Option[T]): T = {
		while (true) {
			try {
				println(request)
				val value = f()
				if (value.isDefined)
					return value.get
			} catch {
				case t: Throwable => println(s"invalid value: $t")
			}
		}
		throw new AssertionError("should not reach this")
	}

	def using[A <: {def close() : Unit}, B](resource: A)(f: A => B): B =
		try {
			f(resource)
		} finally {
			resource.close()
		}

	def mapToIndexes(value: String): Array[Int] = value.split(",").map(_.toInt - 1)

	def extractHValues(value: String): Array[Double] = value
	 .split(",")
	 .map(_.replaceFirst("0[.,]", ""))
	 .map(v => s"0.$v".toDouble)

	def createInstances(problems: Array[Problem], hValues: Array[Double]) =
		problems.map(p => hValues.map(h => Instance(p, h))).flatten
}
