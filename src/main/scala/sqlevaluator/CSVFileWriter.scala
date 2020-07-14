package sqlevaluator

import java.io.{File, PrintWriter}

sealed case class JsonWriter(file:File) extends Writer[List[String]]{
  private val writer = new PrintWriter(file)
  private val seperator = ","

  def writeHeader(header:List[String]) = writer.write(header.mkString(seperator))

  def write(data:List[String]) = writer.write("\n"+data.mkString(seperator))

  def close = writer.close()
}
