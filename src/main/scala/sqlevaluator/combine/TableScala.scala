package sqlevaluator.combine

import jsonsqlmodel.Table.ColumnDef
import jsonsqlmodel.TableDecl

case class ColumnDefinition(srcTable: TableDecl, columnName: ColumnDef)
case class TableScala(columns: List[ColumnDefinition], rows: List[List[String]])
