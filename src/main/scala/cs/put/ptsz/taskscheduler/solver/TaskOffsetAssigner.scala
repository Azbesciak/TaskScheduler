package cs.put.ptsz.taskscheduler.solver


object TaskOffsetAssigner {
	def assign(tasks: Array[Task], startOffset: Int): Array[TaskSchedule] = {
		var lastEndTime = startOffset
		tasks.map(t => {
			val startTime = lastEndTime
			lastEndTime += t.time
			TaskSchedule(t, startTime)
		})
	}
}