package cs.put.ptsz.taskscheduler.input.interactive

import java.io.File

import cs.put.ptsz.taskscheduler.Util.executeTillValid

import scala.io.StdIn


class InteractiveProblemsProvider(private val filePath: String) {
	private val files = getFilesInDir(filePath)

	def list() = files.zipWithIndex.foreach { case (f, i) => println(s"$i: ${f.getName}") }

	def getFile: File = {
		executeTillValid("choose file:", () => {
			val index = StdIn.readInt()
			if (index >= 0 && index < files.length)
				Some(files(index))
			else {
				println(s"invalid index $index, allowed [0..${files.length - 1}]")
				None
			}
		})
	}

	def getFilesInDir(path: String): Array[File] = {
		val file = new File(filePath)
		if (!file.exists()) {
			throw new IllegalArgumentException("file does not exists")
		} else if (file.isFile) {
			Array(file)
		} else {
			file.listFiles()
		}
	}
}