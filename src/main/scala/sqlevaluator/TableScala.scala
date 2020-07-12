package sqlevaluator

import jsonsqlparser.Table.ColumnDef
import jsonsqlparser.TableDecl

case class ColumnDefinition(srcTable: TableDecl, columnName: ColumnDef)
case class TableScala(columns: List[ColumnDefinition], rows: List[List[String]])
