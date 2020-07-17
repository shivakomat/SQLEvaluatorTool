package sqlevaluator.validator

import jsonsqlmodel.Condition.Op
import jsonsqlmodel.Table.ColumnDef
import jsonsqlmodel._
import utils.JsonUtils.{getJsonOfTerm, getJsonValue, getRightLiteralValue}

import scala.collection.JavaConversions._
import scala.util.Try

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

  private def incompatibleType(condition: Condition): Boolean =
    condition.op match {
      case Op.GT => checkForNumericType(condition)
      case Op.LT => checkForNumericType(condition)
      case Op.NE => checkForSameType(condition)
      case Op.EQ => checkForSameType(condition)
      case Op.LE => checkForNumericType(condition)
      case Op.GE => checkForNumericType(condition)
      case _ => true
    }

  private def checkForNumericType(condition: Condition): Boolean = {
    val lColDef = getColumnDef(condition.left)
    val r = getJsonValue(condition.right)
    if((r \ "column").isDefined) {
      val rColDef = getColumnDef(condition.right)
      !(lColDef.`type` == SqlType.INT && rColDef.`type` == SqlType.INT)
    } else {
      val r = getJsonValue(condition.right)
      Try((r \ "literal").as[Int]).isFailure
    }
  }


  private def checkForSameType(condition: Condition): Boolean = {
    val lColDef = getColumnDef(condition.left)
    val r = getJsonValue(condition.right)
    if((r \ "column").isDefined) {
      val rColDef = getColumnDef(condition.right)
      lColDef.`type`.name != rColDef.`type`.name
    } else {
      val r = getJsonValue(condition.right)
      lColDef.`type` match {
        case SqlType.INT => Try((r \ "literal").as[Int]).isFailure
        case SqlType.STR => Try((r \ "literal").as[String]).isFailure
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

