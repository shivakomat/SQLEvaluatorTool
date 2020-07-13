package sqlevaluator

import jsonsqlparser.Table.ColumnDef
import jsonsqlparser._
import play.api.libs.json.{JsValue, Json}

import scala.collection.JavaConversions._
import scala.util.Try

class QueryValidator(query: Query, tables: List[Table]) {

  def run(): Either[String, Query] = {
    val tablesNames = query.from.map(e => e.source).mkString(",")
    for {
      validatedForAmbiguity <- validateForAmbiguousCols(tablesNames)
      inCompatibleTypes <- validateForIncompatibleConditionDataTypes()
    } yield inCompatibleTypes
  }

  def validateForAmbiguousCols(tableNames: String): Either[String, Query] = {
    val ambiguousColumns = query.select.filter(areThereAnyAmbiguousColumns)
    if(ambiguousColumns.nonEmpty) Left("ERROR: Column reference \""+ ambiguousColumns.map(e => e.name).mkString(",") + "\" are ambiguous; present in multiple tables: " + tableNames + ".")
    else Right(query)
  }

  def areThereAnyAmbiguousColumns(selection: Selector): Boolean = {
     val r = tables.zip(query.from).flatMap(t => t._1.columns.filter(c => {
       if(selection.source.table == null) c.name == selection.source.name
       else (c.name == selection.name) && (t._2.source == selection.source.table)
     }))
    r.size > 1
  }

  private def getJsonValueOfTerm(t: Term): JsValue =
    Json.parse(t.toString)

  private def getJsonOfTerm(c: Term): (String, String) = {
    val t = getJsonValueOfTerm(c)
    val tableName = Try((t \ "column" \ "table").as[String]).getOrElse(null)
    val colName = (t \ "column" \ "name").as[String]
    (colName, tableName)
  }

  private def validateForIncompatibleConditionDataTypes() = {
    val invalidTypes = query.where.filter(condition => incompatibleType(condition))
    if(invalidTypes.nonEmpty) {
      val invalids =
        invalidTypes.map(c => {
          if((getJsonValueOfTerm(c.right) \ "column").isDefined) "[ op -> \"" + c.op.symbol + "\" : " + getColumnDef(c.left).`type`.name + " and " + getColumnDef(c.right).`type`.name + "]"
          else "[\"" + c.op.symbol + "\"" + getColumnDef(c.left).`type`.name + getRightLiteralValue(getJsonValueOfTerm(c.right)) + "]"
      })
      Left("ERROR: Incompatible types to " + invalids.mkString(" || "))
    }
    else Right(query)
  }

  private def getRightLiteralValue(rightSide: JsValue): Option[String] =
    Try {
      Try((rightSide \ "literal").as[Int].toString).getOrElse((rightSide \ "literal").as[String])
    }.toOption

  private def incompatibleType(condition: Condition): Boolean = {
    val lColDef = getColumnDef(condition.left)
    val r = getJsonValueOfTerm(condition.right)
    if((r \ "column").isDefined) {
      val rColDef = getColumnDef(condition.right)
      lColDef.`type`.name != rColDef.`type`.name
    } else {
      val r = getJsonValueOfTerm(condition.right)
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

object QueryValidator {
  def apply(query: Query, tables: List[Table]): Either[String, Query] =
    new QueryValidator(query, tables).run()
}