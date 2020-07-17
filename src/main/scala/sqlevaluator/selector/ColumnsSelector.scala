package sqlevaluator.selector

import jsonsqlmodel.Selector
import sqlevaluator.combine.ColumnDefinition

sealed class ColumnsSelector(columns: List[ColumnDefinition], selections: List[Selector]) {

  def run(): Seq[(ColumnDefinition, Int)] = {
    val colsWithIndexes = columns.zipWithIndex
    selections.flatMap(e => {
      colsWithIndexes.filter(c => {
        if(e.source.table != null) (e.source.name == c._1.columnName.name) && (e.source.table == c._1.srcTable.name)
        else e.name == c._1.columnName.name
      })
    })
  }

}

object ColumnsSelector {
  def apply(columns: List[ColumnDefinition], selections: List[Selector]): Seq[(ColumnDefinition, Int)] =
      new ColumnsSelector(columns, selections).run()
}


//def run(): Seq[(ColumnDefinition, Int)] = {
//  val colsWithIndexes = columns.zipWithIndex
//  println(colsWithIndexes)
//  println(selections.toList)
//  selections.flatMap(e => {
//  println("selector ->" + e)
//  println("selector ->" + e.source.name)
//  println("selector ->" + e.source.table)
//  val r = colsWithIndexes.filter(c => {
//  if(e.source.table != null) {
//  println("c -> " + c._1.columnName.name)
//  println("c -> " + c._1.srcTable.name)
//  (e.source.name == c._1.columnName.name) && (e.source.table == c._1.srcTable.name)
//}
//  else e.name == c._1.columnName.name
//})
//  println(r)
//  r
//})
//}