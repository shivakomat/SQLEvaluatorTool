package sqlevaluator

import jsonsqlparser.{Table, TableDecl}

import scala.collection.JavaConversions._

sealed class JoinTables {

  def run(tables: List[Table], tableNames: List[TableDecl]): TableScala = {
    val rowsOfAllTables = tables.map(_.rows.toArray.toList)
    val rows = joinRows(rowsOfAllTables).map(row => row.flatMap(r => convertToListOfStrings(r)))
    val columns = tables.zip(tableNames).flatMap(t => { t._1.columns.map(ColumnDefinition(t._2, _))})
    TableScala(columns, rows)
  }

  private def convertToListOfStrings(row: Any): List[String] =
    row.toString.toCharArray.toSeq.filterNot(e => e == '[' || e == ']' || e == ' ').mkString.split(",").toList

  private def joinRows(rows: List[List[Any]]): List[List[Any]] =
    rows match {
      case Nil => List(Nil)
      case row :: rowSet => {
        for {
          item <- row;
          combination <- joinRows(rowSet)
        } yield item :: combination
      }
    }
}

object JoinTables {

  def apply(tables: List[Table], tableNames: List[TableDecl]): TableScala = {
    new JoinTables().run(tables, tableNames)
  }
}