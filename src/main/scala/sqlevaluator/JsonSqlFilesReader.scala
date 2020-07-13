package sqlevaluator

import com.fasterxml.jackson.core.JsonProcessingException
import jsonsqlparser.Query
import utils.JacksonUtil

object JsonSqlFilesReader {

  def apply(sqlJsonFile: String): Either[String, Query] =
      try Right(JacksonUtil.readFromFile(sqlJsonFile, classOf[Query]))
      catch {
        case ex: JsonProcessingException =>
          Left("Error loading \"" + sqlJsonFile + "\" as query JSON: " + ex.getMessage)
      }
}