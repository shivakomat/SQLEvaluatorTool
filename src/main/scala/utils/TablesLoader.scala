package utils

import java.io.File
import java.util

import com.fasterxml.jackson.core.JsonProcessingException
import jsonsqlparser.{Query, Table, TableDecl}

object TablesLoader {

  def apply(tablesToLoad: util.ArrayList[TableDecl], tablesDir: String): util.ArrayList[Table] = {

    val tables: util.ArrayList[Table] = new util.ArrayList[Table]()

    tablesToLoad.forEach { tableDecl =>
      val tableSourcePath =   tablesDir + File.separator + (tableDecl.source + ".table.json")
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