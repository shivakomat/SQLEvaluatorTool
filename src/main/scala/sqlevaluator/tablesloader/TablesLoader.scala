package sqlevaluator.tablesloader

import java.io.File
import java.util

import com.fasterxml.jackson.core.JsonProcessingException
import jsonsqlmodel.{Table, TableDecl}
import utils.JacksonUtil
import scala.collection.JavaConversions._
import scala.util.{Failure, Success, Try}

object TablesLoader {

  def apply(tablesToLoad: util.ArrayList[TableDecl], tablesDir: String): Either[String, util.ArrayList[Table]] = {
    val tables: util.ArrayList[Table] = new util.ArrayList[Table]()

    val numberOfTableDontExist = tablesToLoad.flatMap(tableDecl => {
      val tableSourcePath =   tablesDir + File.separator + (tableDecl.source + ".table.json")
      if(!scala.reflect.io.File(tableSourcePath).exists) Some(tableDecl.source)
      else None
    })

    if(numberOfTableDontExist.nonEmpty)
      return Left("Error: following tables are not found " + numberOfTableDontExist.mkString(","))

    tablesToLoad.forEach { tableDecl =>
      val tableSourcePath = tablesDir + File.separator + (tableDecl.source + ".table.json")
      Try(JacksonUtil.readFromFile(tableSourcePath, classOf[Table])) match {
        case Success(table) => tables.add(table)
        case Failure(ex: JsonProcessingException) =>
          Left("Error loading \"" + tableSourcePath + "\" as table JSON: " + ex.getMessage)
      }
    }

    Right(tables)
  }

}
