package sqlevaluator

import com.fasterxml.jackson.core.JsonProcessingException
import jsonsqlparser.Query
import utils.JacksonUtil

object JsonSqlFilesReader {

  private val dirPath = "/Users/shivakomatreddy/IdeaProjects/AirTableSQLEvaluator/src/main/scala/sqlevaluator/examples"

  def apply(sqlJsonFile: String): Query = {
    var query: Query = null
    try query = JacksonUtil.readFromFile(sqlJsonFile, classOf[Query])
    catch {
      case ex: JsonProcessingException =>
        System.err.println("Error loading \"" + sqlJsonFile + "\" as query JSON: " + ex.getMessage)
        System.exit(1)
    }
    query
  }

}