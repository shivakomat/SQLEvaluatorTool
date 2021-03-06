import java.io.File

import config.AppArgumentsParser
import sqlevaluator.combine.JoinTables
import sqlevaluator.comparator.Comparator
import sqlevaluator.selector.ColumnsSelector
import sqlevaluator.tablesloader
import sqlevaluator.validator.QueryValidator
import utils.{DataWriter, JsonSqlFilesReader}

import scala.collection.JavaConversions._

object Main extends App {
    val parsedConfig = AppArgumentsParser(args) match {
        case Left(error) => {
            System.err.println(error)
            System.err.println("Usage: COMMAND <table-folder> <sql-json-file> <output-file>")
            System.exit(1);
            None
        }
        case Right(config) => Some(config)
    }

    val config = parsedConfig.get

    println("Argument Selections :::::::::::::::::::::::")
    println()
    println("Output File : " + config.outputFile)
    println("SQL Json File : " + config.sqlJsonFile)
    println("Table Folder : " + config.tableFolder)


    val sqlJsonFile = config.sqlJsonFile
    val tablesDataDir = config.tableFolder
    val outputFolder = config.outputFile

    val result = for {
        // reads the input sql json file and build a Query object
        q <- JsonSqlFilesReader(sqlJsonFile)

        // loads all the tables selected in the Query above
        tables <- tablesloader.TablesLoader(q.from, tablesDataDir)

        // validates query column selections and where conditions
        validatorResults <- QueryValidator(q, tables.toList)

        // produces product of all the tables data set
        joinedTable <- Right(JoinTables(tables.toList, q.from.toList))

        // reduces the data set based on conditions set in the query where clause
        finalTable <- Right(Comparator(joinedTable, q.where.toList))

        // selects the user columns selected in the query and returns the final table
        finalTableWithSelections <- Right(ColumnsSelector(finalTable.columns, q.select.toList))

    } yield (finalTable, finalTableWithSelections)

    result match {
        case Left(errors) =>
            System.err.println(errors)
            DataWriter.error(outputFolder, errors)
            System.exit(1)
        case Right(table) =>
            println(table._2.map(s => s._1.columnName.name).mkString(","))
            table._1.rows.foreach(row => println(table._2.map(s => row(s._2)).mkString(",")))
            DataWriter.run(outputFolder, table._1, table._2)
    }
}