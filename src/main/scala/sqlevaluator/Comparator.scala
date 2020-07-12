package sqlevaluator

import jsonsqlparser.Condition.Op
import jsonsqlparser.{Condition, Term}
import play.api.libs.json.{JsValue, Json}
import scala.util.Try

sealed class Comparator(table: TableScala) {

  def run(conditions: List[Condition]): TableScala =
    table.copy(rows = table.rows.flatMap(row => rowByConditions(conditions, Some(row))))

  @scala.annotation.tailrec
  private def rowByConditions(conditions: List[Condition], row: Option[List[String]]): Option[List[String]] =
    conditions match {
      case Nil => row
      case conditions => {
        if(row.nonEmpty) {
          val (lJson, rJson) = (getJsonOfTerm(conditions.head.left), getJsonOfTerm(conditions.head.right))
          val rValue = getRightLiteralValue(rJson).getOrElse(row.get(getColumnPosition(rJson)))
          val rowFiltered = applyCondition(row.get, colPos = getColumnPosition(lJson), op = conditions.head.op, rValue = rValue)
          val newRow = if (rowFiltered) row else None
          rowByConditions(conditions.tail, newRow)
        } else None
      }
    }

  private def getJsonOfTerm(t: Term): JsValue =
    Json.parse(t.toString)

  private def getColumnPosition(t: JsValue): Int = {
    val tableName = (t \ "column" \ "table").as[String]
    val colName = (t \ "column" \ "name").as[String]

    table.columns.zipWithIndex
      .filter(col => {
        if(tableName != "null") col._1.srcTable.name == tableName && col._1.columnName.name == colName
        else col._1.columnName.name == colName
      }).head._2
  }

  private def applyCondition(row: List[String], colPos:  Int, op: Op, rValue: String): Boolean =
    op match {
      case Op.GT => (row(colPos).toInt > rValue.toInt)
      case Op.LT => (row(colPos).toInt < rValue.toInt)
      case Op.NE => (row(colPos) != rValue)
      case Op.EQ => (row(colPos) == rValue)
      case Op.LE => (row(colPos).toInt <= rValue.toInt)
      case Op.GE => (row(colPos).toInt >= rValue.toInt)
      case _ => true
    }

  private def getRightLiteralValue(rightSide: JsValue): Option[String] =
    Try {
      Try((rightSide \ "literal").as[Int].toString).getOrElse((rightSide \ "literal").as[String])
    }.toOption
}

object Comparator {
  def apply(table: TableScala, conditions: List[Condition]): TableScala = new Comparator(table).run(conditions)
}