package sqlevaluator.queryvalidator

import jsonsqlparser.{Query, Selector, Table}
import scala.collection.JavaConversions._

sealed class AmbiguousColumnsValidation(query: Query,  tables: List[Table]) {

  def run(): Either[String, Query] = {
    val ambiguousColumns = query.select.filter(areThereAnyAmbiguousColumns)
    val tableNames = query.from.map(e => e.source).mkString(",")
    if(ambiguousColumns.nonEmpty)
      Left("ERROR: Column reference \""+ ambiguousColumns.map(e => e.name).mkString(",") + "\" are ambiguous; present in multiple tables: " + tableNames + ".")
    else Right(query)
  }

  private def areThereAnyAmbiguousColumns(selection: Selector): Boolean = {
    val r = tables.zip(query.from).flatMap(t => t._1.columns.filter(c => {
      if(selection.source.table == null) c.name == selection.source.name
      else (c.name == selection.name) && (t._2.source == selection.source.table)
    }))
    r.size > 1
  }

}

object AmbiguousColumnsValidation {
  def apply(query: Query, tables: List[Table]): Either[String, Query] = new AmbiguousColumnsValidation(query, tables).run()
}
