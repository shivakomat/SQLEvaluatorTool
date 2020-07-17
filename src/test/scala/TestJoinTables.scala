import java.util

import jsonsqlmodel._
import org.scalatest.FlatSpec
import sqlevaluator.combine.JoinTables
import testdatabuilders.{TestQueryFixtureBuilder, TestTableFixtureBuilder}

import scala.collection.JavaConversions._

class TestJoinTables extends FlatSpec with TestQueryFixtureBuilder with TestTableFixtureBuilder {

  val queryTables =  generateFromTables(Map("a1" -> "a", "a2" -> "a", "b" -> "b"))

  val table1 = generateTable(columns = Seq(("name", SqlType.STR), ("age", SqlType.INT)),
                             rows = Seq(Seq("Alice", new Integer(20)),
                                       Seq("Bob", new Integer(30)),
                                       Seq("Eve", new Integer(40))))

  val table2 = generateTable(columns = Seq(("name", SqlType.STR), ("age", SqlType.INT)),
                             rows = Seq(Seq("Alice", new Integer(20)),
                                        Seq("Bob", new Integer(30)),
                                        Seq("Eve", new Integer(40))))

  val table3 = generateTable(columns = Seq(("name", SqlType.STR), ("distance", SqlType.INT)),
                             rows = Seq(Seq("Q", new Integer(25)),
                                        Seq("R", new Integer(32))))

  "joinTables" should "return a table of cross joined of all the tables into one table" in {
        val tables: util.ArrayList[Table] = new util.ArrayList()
        tables.add(table1)
        tables.add(table2)
        tables.add(table3)

        val totalNumberOfColumns = tables.map(t => t.columns.size()).sum
        val totalNumberOfRecords = (tables.size() * totalNumberOfColumns)
        val joinedTable = JoinTables(tables.toList, queryTables.toList)

        assert(joinedTable.rows.size == totalNumberOfRecords)
        assert(joinedTable.columns.size == totalNumberOfColumns)
        assert(joinedTable.rows.toList.head == List("Alice", "20", "Alice", "20", "Q", "25"))
  }

  it should "return same table when the list of tables to join is only 1" in {
        val tables: util.ArrayList[Table] = new util.ArrayList()
        tables.add(table1)

        val joinedTable = JoinTables(tables.toList, queryTables.toList)

        assert(joinedTable.rows.size == table1.rows.size)
        assert(joinedTable.columns.size == table1.columns.size)
  }


}
