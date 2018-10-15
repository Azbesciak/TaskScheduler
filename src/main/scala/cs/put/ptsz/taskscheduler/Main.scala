package cs.put.ptsz.taskscheduler

import java.io.{File, FileInputStream}
import java.util.Scanner

import Control._

import scala.io.{Source, StdIn}
import scala.util.matching.Regex

object Main extends App {
  def getFile(files: Array[File]): File = {
    while (true) {
      println("choose file:")
      val index = StdIn.readInt()
      if (index >= 0 && index < files.length)
        return files(index)
      println(s"invalid index $index, allowed [0..${files.length - 1}")
    }
    null
  }
  val regex = new Regex("\\s+")
  def getProblems(scanner: Scanner): Array[Problem] = {
    val problemsCount = scanner.nextInt()
    val problems = new Array[Problem](problemsCount)
    for (i <- 0 until problemsCount) {
      problems(i) = createProblem(scanner)
    }
    problems
  }

  def createProblem(scanner: Scanner): Problem = {
    val tasksCount = scanner.nextInt()
    val tasks = new Array[Task](tasksCount)
    for (taskId <- 0 until tasksCount) {
      tasks(taskId) = Task(scanner.nextInt(), scanner.nextInt(), scanner.nextInt())
    }
    Problem(tasks)
  }

  private val files: Array[File] = new File("src/main/resources").listFiles()
  files.zipWithIndex.foreach { case (f, i) => println(s"$i: $f") }
  val file = getFile(files)

  val problems = using(new Scanner(new FileInputStream(file))) (getProblems)
  println(problems.zipWithIndex.map{case (t, i) => s"$i: ${t.tasks.mkString(",")}"}.mkString("\n"))
}

