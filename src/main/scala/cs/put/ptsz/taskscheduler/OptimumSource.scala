package cs.put.ptsz.taskscheduler

import java.io.File

import scala.io.Source
import scala.util.Properties


object OptimumSource {
	val OPTIMUM_SOURCE_PROP = "optimumFilePath"
	private lazy val upperBounds: Map[InstanceParams, UpperBoundValue] = getOptimaMap()

	def apply(params: InstanceParams): UpperBoundValue = upperBounds(params)
	def canUse() = Properties.propIsSet(OPTIMUM_SOURCE_PROP)

	private def getOptimaMap() = {
		val optimumPath = Properties.propOrNull(OPTIMUM_SOURCE_PROP)
		require(optimumPath != null, s"property $OPTIMUM_SOURCE_PROP not set")
		val file = new File(optimumPath)
		require(file.exists(), s"optimum file $optimumPath not exists")
		val lines = Source.fromFile(file)
		 .getLines()
		 .filterNot(_.isBlank)
		 .map(fixLine)
		 .toArray
		val startIndexes = lines.zipWithIndex.filter {
			case (line, _) => line(0).startsWith("n")
		}.map(_._2) appended lines.length

		startIndexes.sliding(2)
		 .map(w => lines.slice(w(0), w(1)))
		 .flatMap(mapProblem).toMap
	}

	private def mapProblem(problemLines: Array[Array[String]]) = {
		val nStr = problemLines(0)(0)
		require(nStr.startsWith("n="), "malformed optima format - unknown n")
		val instanceSize = nStr.drop(2).toInt
		val hValues = problemLines(0).drop(2).map(_.drop(2).toDouble)
		problemLines.drop(1).map(l => {
			require(l(0).startsWith("k="), "malformed optima format - invalid k")
			val kVal = l(0).drop(2).toInt
			val upperBounds = l.drop(2).map(getUpperBound)
			upperBounds.zip(hValues).map {
				case (ub, h) => (InstanceParams(instanceSize, kVal, h), ub)
			}
		}).flatten
	}

	private def getUpperBound(value: String) = {
		val isUpperBound = value.contains("*")
		val upperBoundValue = value.replace("*", "").toInt
		UpperBoundValue(upperBoundValue, isUpperBound)
	}

	private def fixLine(line: String) = line.trim()
	 .replaceAll("\\s*=\\s*", "=")
	 .replaceAll(",", "")
	 .split("\\s+")

}

case class UpperBoundValue(value: Int, isOptimum: Boolean) {
	override def toString: String = s"$value${if (isOptimum) "*" else ""}"
}

case class InstanceParams(size: Int, id: Int, h: Double)
