import jsonsqlparser.Condition.Op
import jsonsqlparser.Term.{Column, Literal}
import jsonsqlparser._
import org.scalatest.FlatSpec
import sqlevaluator.JoinTables
import utils.TablesLoader

import scala.collection.JavaConversions._

class JoinTablesTest extends FlatSpec {

  val selectors = new java.util.ArrayList[Selector]()

  val s1 = new Selector("a1_name", new ColumnRef("name", "a1"))
  val s2 = new Selector("age", new ColumnRef("age", "a1"))
  val s3 = new Selector("a2_name", new ColumnRef("name", "a2"))
  val s4 = new Selector("distance", new ColumnRef("distance", "b"))

  selectors.add(s1)
  selectors.add(s2)
  selectors.add(s3)
  selectors.add(s4)

  val f1  = new TableDecl("a1", "a")
  val f2  = new TableDecl("a2", "a")
  val f3  = new TableDecl("b", "b")

  val froms =  new java.util.ArrayList[TableDecl]()

  froms.add(f1)
  froms.add(f2)
  froms.add(f3)

  val whereConditions = new java.util.ArrayList[Condition]()
  val condition1 = new Condition(Op.NE, new Column(new ColumnRef("name", "a1")), new Literal("Bob"))
  whereConditions.add(condition1)

  val query =  new Query(selectors, froms, whereConditions)
  val tables = TablesLoader(tablesToLoad = query.from, "/Users/shivakomatreddy/IdeaProjects/AirTableSQLEvaluator/src/main/scala/sqlevaluator/examples")

  "joinTables" should "return a table of cross joined of all the tables into one table" in {
        val totalNumberOfColumns = tables.map(t => t.columns.size()).sum
        val totalNumberOfRecords = (tables.size() * totalNumberOfColumns)
        val joinedTable = JoinTables(tables.toList, froms.toList)
        assert(joinedTable.rows.size==totalNumberOfRecords)
        assert(joinedTable.columns.size == 6)
  }

}
