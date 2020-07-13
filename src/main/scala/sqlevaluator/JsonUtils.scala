package sqlevaluator

import jsonsqlparser.Term
import play.api.libs.json.{JsValue, Json}
import scala.util.Try

object JsonUtils {

  def getJsonValue(t: Term): JsValue =
    Json.parse(t.toString)

  def getJsonOfTerm(c: Term): (String, String) = {
    val t = getJsonValue(c)
    getTermTableNameAndColName(t)
  }

  def getTermTableNameAndColName(t: JsValue): (String, String) = {
    val tableName = Try((t \ "column" \ "table").as[String]).getOrElse(null)
    val colName = (t \ "column" \ "name").as[String]
    (colName, tableName)
  }

  def getRightLiteralValue(rightSide: JsValue): Option[String] =
    Try {
      Try((rightSide \ "literal").as[Int].toString).getOrElse((rightSide \ "literal").as[String])
    }.toOption
}
