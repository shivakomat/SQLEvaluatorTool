package sqlevaluator

import jsonsqlparser.Selector

sealed class SelectorCompute(columns: List[ColumnDefinition], selections: List[Selector]) {

  def run(): Seq[(ColumnDefinition, Int)] = {
    val colsWithIndexes = columns.zipWithIndex
    selections.flatMap(e => {
      colsWithIndexes.filter(c => {
        if(e.source.table != null) (e.name == c._1.columnName.name) && (e.source.table == c._1.srcTable.name)
        else e.name == c._1.columnName.name
      })
    })
  }
}

object SelectorCompute {

  def apply(columns: List[ColumnDefinition], selections: List[Selector]): Seq[(ColumnDefinition, Int)] = {
    new SelectorCompute(columns, selections).run()
  }

}