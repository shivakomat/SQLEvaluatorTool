import jsonsqlparser.Condition.Op
import jsonsqlparser.Term.{Column, Literal}
import jsonsqlparser.{ColumnRef, Condition, Query, Selector, TableDecl}

trait TestQueryBuilder {

  trait TCol
  case class TestColumn(val name: String, val table: String) extends TCol
  case class TestLiteral(val litValue: String) extends  TCol


  def getQuery(selectors: Map[String, TestColumn], froms:  Map[String, String], conditions: Map[Op, (TestColumn, TCol)]): Query = {
    new Query(generateSelectors(selectors),
              generateFromTables(froms),
              generateConditions(conditions))
  }


  def generateSelectors(testData: Map[String, TestColumn]): java.util.ArrayList[Selector] = {
    val selectors = new java.util.ArrayList[Selector]()
    testData foreach { data =>
      selectors.add(new Selector(data._1, new ColumnRef(data._2.name, data._2.table)))
    }
    selectors
  }

  def generateFromTables(tesData: Map[String, String]): java.util.ArrayList[TableDecl] = {
    val froms =  new java.util.ArrayList[TableDecl]()
    tesData foreach { data =>
      froms.add(new TableDecl(data._1, data._2))
    }
    froms
  }

  def generateConditions(tesData: Map[Op, (TestColumn, TCol)]): java.util.ArrayList[Condition] = {
    val conditions =  new java.util.ArrayList[Condition]()
    tesData foreach { data =>
      val newCondition = data._2._2 match {
        case literal: TestLiteral =>
          new Condition(data._1, new Column(new ColumnRef(data._2._1.name, data._2._1.table)), new Literal(literal.litValue))
        case _ =>
          val instance = data._2._2.asInstanceOf[TestColumn]
          new Condition(data._1, new Column(new ColumnRef(data._2._1.name, data._2._1.table)), new Column(new ColumnRef(instance.name, instance.table)))
      }
      conditions.add(newCondition)
    }
    conditions
  }


}