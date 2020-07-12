package utils

import java.io.File
import java.util

import com.fasterxml.jackson.core.JsonProcessingException
import jsonsqlparser.{Table, TableDecl}

object TablesLoader {

  private val dirPath = "/Users/shivakomatreddy/IdeaProjects/AirTableSQLEvaluator/src/main/scala/sqlevaluator/examples"

  def apply(tablesToLoad: util.ArrayList[TableDecl]): util.ArrayList[Table] = {

    val tables: util.ArrayList[Table] = new util.ArrayList[Table]()

    tablesToLoad.forEach { tableDecl =>
      val tableSourcePath =   dirPath + File.separator + (tableDecl.source + ".table.json")
      var table: Table = null
      try table = JacksonUtil.readFromFile(tableSourcePath, classOf[Table])
      catch {
        case ex: JsonProcessingException =>
          System.err.println("Error loading \"" + tableSourcePath + "\" as table JSON: " + ex.getMessage)
          System.exit(1)
      }
      tables.add(table)
    }

    tables
  }


}
