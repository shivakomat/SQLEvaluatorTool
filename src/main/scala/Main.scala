import sqlevaluator.{Comparator, JoinTables, JsonSqlFilesReader, QueryValidator}
import utils.TablesLoader

import scala.collection.JavaConversions._

object Main extends App {
//    val parsedConfig = AppArgumentsParser(args) match {
//        case Left(error) => {
//            System.err.println(error)
//            System.err.println("Usage: COMMAND <table-folder> <sql-json-file> <output-file>")
//            System.exit(1);
//            None
//        }
//        case Right(config) => Some(config)
//    }
//
//    val config = parsedConfig.get
//
//    println("Argument Selections :::::::::::::::::::::::")
//    println("Output File : " + config.outputFile)
//    println("SQL Json File : " + config.sqlJsonFile)
//    println("Table Folder : " + config.tableFolder)

    val result = for {
        q <- JsonSqlFilesReader("/Users/shivakomatreddy/IdeaProjects/AirTableSQLEvaluator/src/main/scala/sqlevaluator/examples/error-1.sql.json")
        tables <- Right(TablesLoader(q.from, "/Users/shivakomatreddy/IdeaProjects/AirTableSQLEvaluator/src/main/scala/sqlevaluator/examples"))
        validatorResults <- QueryValidator(q, tables.toList)
        joinedTable <- Right(JoinTables(tables.toList, q.from.toList))
        finalTable <- Right(Comparator(joinedTable, q.where.toList))
    } yield finalTable

    result match {
        case Left(errors) =>
            System.err.println(errors)
            System.exit(1)
        case Right(r) =>
            r.rows.foreach(println)
    }
}