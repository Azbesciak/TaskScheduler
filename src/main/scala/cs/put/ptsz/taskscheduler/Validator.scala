package cs.put.ptsz.taskscheduler

import java.io.File

import cs.put.ptsz.taskscheduler.Util.mapToIndexes
import cs.put.ptsz.taskscheduler.cost.{CostFunction, OneMachineScheduleEndTimeCostFunction}
import cs.put.ptsz.taskscheduler.input.ProblemParser
import cs.put.ptsz.taskscheduler.solver._

import scala.io.Source
import scala.util.matching.Regex

case class SolutionInstanceParams(
	h: Option[String],
	k: Option[String],
	n: Option[String]
)

object InstanceParamsParser {
	private val solutionFileRegex = "(n(\\d+))?(k([\\d,]+))(h(0[,.])?(\\d+))\\.txt"
	 .r("n", "nVal", "k", "kVal", "h", "prefix", "hVal")

	private def getHValue(params: Regex.MatchIterator): Option[String] = {
		Some(params.group("hVal"))
	}

	def parse(solutionFilePath: String) = {
		val params: Regex.MatchIterator = solutionFileRegex
		 .findAllIn(solutionFilePath)
		SolutionInstanceParams(
			Some(params.group("hVal")),
			Some(params.group("kVal")),
			Some(params.group("nVal"))
		)
	}
}

class StringSolutionValidator(costFunctionSource: Instance => CostFunction) {
	def validate(solutionString: String, instance: Instance): ValidationResult = {
		val values = solutionString.split("\\s")
		val f = values(0).toInt
		val h = values(1).toDouble
		val offSet = values(2).toInt
		val tasks = instance.problem.tasks

		require(tasks.length == values.length - 3,
			s"invalid problem tasks length - ${tasks.length} vs ${values.length - 3} in solution"
		)
		val orderedTasks = values.drop(3).map(v => tasks(v.toInt))
		val scheduledTasks = TaskOffsetAssigner.assign(orderedTasks, offSet)
		val solution = Solution(scheduledTasks)
		ValidationResult(
			f,
			costFunctionSource(instance).cost(solution).value,
			instance
		)
	}
}

case class ValidationResult(
	given: Int,
	validated: Int,
	instance: Instance
) {
	val isValid: Boolean = given == validated
}


object Validator extends App {
	private def extractHValue(params: SolutionInstanceParams) = {
		val hString = params.h
		 .getOrElse({
			 require(args.length == 3, "h value not passed neither in args nor in solution name")
			 args(2)
		 })
		 .replaceFirst("0[.,]", "")
		s"0.$hString".toDouble
	}

	require(args.length == 2 || args.length == 3, {
		"Usage: cmd args: <instance file path> <solution path> " +
		 "[h value - if absent, require in output name in type nkh.txt with values after prefix]"
	})

	private val instanceFile = new File(args(0))
	require(instanceFile.exists(), s"instance file ${args(0)} does not exists"
	)
	private val problems = ProblemParser.load(instanceFile)
	private val solutionFilePath = args(1)
	private val solutionFile = new File(solutionFilePath)
	require(solutionFile.exists(), s"solution $solutionFilePath file does not exists")
	val params = InstanceParamsParser.parse(solutionFilePath)
	private val h = extractHValue(params)
	private val chosenProblems = params.k
	 .map(mapToIndexes)
	 .map(indexes => indexes.map(problems(_)))
	 .getOrElse(problems)
	 .map(Instance(_, h))
	val validator = new StringSolutionValidator(instance => new OneMachineScheduleEndTimeCostFunction(instance))

	private val invalidSolutions = Source.fromFile(solutionFile)
	 .getLines().toArray
	 .zip(chosenProblems)
	 .map(s => validator.validate(s._1, s._2))
	 .filterNot(r => r.isValid)

	if (invalidSolutions.nonEmpty) {
		println("found invalid solutions!")
		invalidSolutions.foreach(println)
	}
}