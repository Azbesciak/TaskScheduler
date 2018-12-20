package cs.put.ptsz.taskscheduler.cost


case class Cost(
	value: Int
) {
	def <(cost: Cost): Boolean = {
		value < cost.value
	}
	def >=(cost: Cost): Boolean = {
		value >= cost.value
	}
}
