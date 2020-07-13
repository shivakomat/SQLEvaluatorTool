package sqlevaluator.queryvalidator

import jsonsqlparser.Table.ColumnDef
import jsonsqlparser.{Condition, Query, SqlType, Table, Term}
import sqlevaluator.JsonUtils.{getJsonOfTerm, getJsonValue, getRightLiteralValue}

import scala.collection.JavaConversions._

sealed class IncompatibleConditionDataTypes(query: Query,  tables: List[Table]) {

  def run(): Either[String, Query] = {
    val invalidTypes = query.where.filter(condition => incompatibleType(condition))
    if(invalidTypes.nonEmpty) {
      val invalids =
        invalidTypes.map(c => {
          if((getJsonValue(c.right) \ "column").isDefined) "[ op -> \"" + c.op.symbol + "\" : " + getColumnDef(c.left).`type`.name + " and " + getColumnDef(c.right).`type`.name + "]"
          else "[\"" + c.op.symbol + "\"" + getColumnDef(c.left).`type`.name + getRightLiteralValue(getJsonValue(c.right)) + "]"
        })
      Left("ERROR: Incompatible types to " + invalids.mkString(" || "))
    }
    else Right(query)
  }

  private def incompatibleType(condition: Condition): Boolean = {
    val lColDef = getColumnDef(condition.left)
    val r = getJsonValue(condition.right)
    if((r \ "column").isDefined) {
      val rColDef = getColumnDef(condition.right)
      lColDef.`type`.name != rColDef.`type`.name
    } else {
      val r = getJsonValue(condition.right)
      lColDef.`type` match {
        case SqlType.INT => (r \ "literal").isInstanceOf[String]
        case SqlType.STR => (r \ "literal").isInstanceOf[Int]
      }
    }
  }

  def getColumnDef(cond: Term): ColumnDef = {
    val term = getJsonOfTerm(cond)
    tables.zip(query.from).flatMap(t => t._1.columns.filter(c => {
      if(term._2 == null) (c.name == term._1)
      else c.name == term._1 && t._2.name == term._2
    })).head
  }

}

object IncompatibleConditionDataTypes {
  def apply(query: Query, tables: List[Table]): Either[String, Query] = new IncompatibleConditionDataTypes(query, tables).run()
}

