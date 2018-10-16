package cs.put.ptsz.taskscheduler


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
}
