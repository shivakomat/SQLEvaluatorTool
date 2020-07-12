import sqlevaluator.{Comparator, JoinTables, JsonSqlFilesReader, SelectorCompute}
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

    // Json file ingested
    val query = JsonSqlFilesReader("/Users/shivakomatreddy/IdeaProjects/AirTableSQLEvaluator/src/main/scala/sqlevaluator/examples/cities-2.sql.json")

    // Load Tables
    val tables = TablesLoader(query.from, "/Users/shivakomatreddy/IdeaProjects/AirTableSQLEvaluator/src/main/scala/sqlevaluator/examples")
    val joinedTable = JoinTables(tables.toList, query.from.toList)
    val finalTable = Comparator(joinedTable, query.where.toList)

    val columnDefinitions = SelectorCompute(finalTable.columns, query.select.toList)


    println(columnDefinitions.map(c => c._1.srcTable.name + "." + c._1.columnName.name).mkString(","))
    finalTable.rows.foreach(row =>  {
        println(columnDefinitions.map(c => row(c._2)).mkString(","))
    })
}