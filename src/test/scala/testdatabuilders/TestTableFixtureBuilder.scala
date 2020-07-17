package testdatabuilders

import java.util
import jsonsqlmodel.Table.ColumnDef
import jsonsqlmodel.{SqlType, Table}
import scala.collection.JavaConversions._


trait TestTableFixtureBuilder {

  def getTables(tables: Seq[Table]): List[Table] = {
    val t: util.ArrayList[Table] = new util.ArrayList()
    tables.forEach(table => t.add(table))
    t.toList
  }

  def generateTable(columns: Seq[(String, SqlType)], rows: Seq[Seq[Object]]): Table = {
    val tableColumns: util.ArrayList[ColumnDef] = new util.ArrayList()
    columns.foreach(c => tableColumns.add(new ColumnDef(c._1, c._2)))

    val tableRows: util.ArrayList[util.ArrayList[Object]] = new util.ArrayList()
    rows.foreach(row => {
      val r: util.ArrayList[Object] = new util.ArrayList()
      row.foreach(data => r.add(data))
      tableRows.add(r)
    })

    new Table(tableColumns, tableRows)
  }


  def getColumns(columns: Seq[(String, SqlType)]): List[ColumnDef] = {
    val tableColumns: util.ArrayList[ColumnDef] = new util.ArrayList()
    columns.foreach(c => tableColumns.add(new ColumnDef(c._1, c._2)))
    tableColumns.toList
  }

}
