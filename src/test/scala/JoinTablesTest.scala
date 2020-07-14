import java.util

import jsonsqlparser.Condition.Op
import jsonsqlparser._
import org.scalatest.FlatSpec
import sqlevaluator.JoinTables
import utils.TablesLoader

import scala.collection.JavaConversions._

class JoinTablesTest extends FlatSpec with TestQueryBuilder {

  val selectors = Map("a1_name"->  TestColumn("name", "a1"),
                      "age"->  TestColumn("age", "a2"),
                      "a2_name"->  TestColumn("name", "a2"),
                      "distance"->  TestColumn("distance", "b"))

  val froms =  Map("a1" -> "a", "a2" -> "a", "b" -> "b")
  val conditions = Map(Op.NE -> (TestColumn("name", "a1"), TestLiteral("Bob")))

  val query = getQuery(selectors, froms, conditions)
  val tables: util.ArrayList[Table] = TablesLoader(tablesToLoad = query.from, "/Users/shivakomatreddy/IdeaProjects/AirTableSQLEvaluator/src/main/scala/sqlevaluator/examples")

  "joinTables" should "return a table of cross joined of all the tables into one table" in {
        val totalNumberOfColumns = tables.map(t => t.columns.size()).sum
        val totalNumberOfRecords = (tables.size() * totalNumberOfColumns)
        val joinedTable = JoinTables(tables.toList, query.from.toList)
        assert(joinedTable.rows.size==totalNumberOfRecords)
        assert(joinedTable.columns.size == 6)
  }

}
