package utils

import java.io.{File, PrintWriter}

import sqlevaluator.combine.{ColumnDefinition, TableScala}

import scala.util.Try

sealed case class DataWriter(file:File, table: TableScala) {
  private val writer = new PrintWriter(file)
  private val seperator = ","
  private val space = " "

  private val fileOpen = "["
  private val fileClosed = "]"

  private val rowBracketOpen = "["
  private val rowBracketClosed = "]"

  def writeErrorMsg(error: String) = {
    writer.write(error)
    writer.close()
  }


  def writeHeader(header: Seq[ColumnDefinition]) = {
    writer.write(fileOpen + "\n" + rowBracketOpen  +
      header.map(c => {rowBracketOpen +  "\"" + c.columnName.name + "\"," + "\"" + c.columnName.`type`.name + "\"" + rowBracketClosed}).mkString(seperator + space) +
      rowBracketClosed + seperator)
  }

  def writeRow(data:List[String]) = {
    writer.write(
      "\n" + space + rowBracketOpen +
      data.map(d => { if(Try(d.toInt).isSuccess) d else "\"" + d + "\""}).mkString(seperator +  space) +
      rowBracketClosed + seperator)
  }

  def close = {
    writer.write("\n" + fileClosed)
    writer.close()
  }

}


object DataWriter {

  def error(fileName: String, msg: String) = {
    val outputFile = new File(fileName)
    new DataWriter(outputFile, null).writeErrorMsg(msg)
  }

  def run(fileName: String, table: TableScala, columns: Seq[(ColumnDefinition, Int)]): Unit = {
    val outputFile = new File(fileName)
    val fileWriter = new DataWriter(outputFile, table)
    fileWriter.writeHeader(columns.map(_._1))
    table.rows.foreach(row => fileWriter.writeRow(columns.map(s => row(s._2)).toList))
    fileWriter.close
  }
}