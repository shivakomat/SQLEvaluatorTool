package utils

import com.fasterxml.jackson.core.JsonProcessingException
import jsonsqlmodel.Query

import scala.util.{Failure, Success, Try}

object JsonSqlFilesReader {

  def apply(sqlJsonFile: String): Either[String, Query] =
      Try(JacksonUtil.readFromFile(sqlJsonFile, classOf[Query])) match {
        case Success(query) => Right(query)
        case Failure(ex: JsonProcessingException) =>
          Left("Error loading \"" + sqlJsonFile + "\" as query JSON: " + ex.getMessage)
      }
}
