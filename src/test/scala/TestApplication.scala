import jsonsqlmodel.Condition.Op
import jsonsqlmodel._
import org.scalatest.FlatSpec
import sqlevaluator.combine.JoinTables
import sqlevaluator.comparator.Comparator
import sqlevaluator.selector.ColumnsSelector
import testdatabuilders.{TestQueryFixtureBuilder, TestTableFixtureBuilder}
import sqlevaluator.validator.QueryValidator

import scala.collection.JavaConversions._

class TestApplication extends FlatSpec with TestQueryFixtureBuilder with TestTableFixtureBuilder {

  trait TestData {
    val from = Map("a1" -> "a", "a2" -> "a", "b" -> "b")


    val selectors = Map("a1_name" -> TestColumn("name", "a1"),
      "age" -> TestColumn("age", "a2"),
      "a2_name" -> TestColumn("name", "a2"),
      "distance" -> TestColumn("distance", "b"))

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
  }



  // Test with no conditions
  "query with no where clause" should "return combined table with selected columns" in new TestData {
    val tables = getTables(Seq(table1, table2, table3))
    val query =  getQuery(selectors, from, Map.empty[Op, (TestColumn, TCol)])

    val validationResults = QueryValidator(query, tables)
    assert(validationResults.isRight)

    val totalNumberOfColumns = tables.map(t => t.columns.size()).sum
    val totalNumberOfRecords = (tables.size() * totalNumberOfColumns)
    val joinedTable = JoinTables(tables, query.from.toList)

    assert(joinedTable.rows.size == totalNumberOfRecords)
    assert(joinedTable.columns.size == totalNumberOfColumns)
    assert(joinedTable.rows.head == List("Alice", "20", "Alice", "20", "Q", "25"))

    val finalTable = Comparator(joinedTable, query.where.toList)

    assert(finalTable.rows.size == joinedTable.rows.size)

    val selectedColumns = ColumnsSelector(finalTable.columns, query.select.toList)

    assert(selectedColumns.size == selectors.size)
  }


  // Test with multiple tables with multiple conditions
  "query with multiple where clauses" should "return filtered combined table with selected columns" in new TestData  {
    val tables = getTables(Seq(table1, table2, table3))

    val where = Map(Op.NE ->(TestColumn("name", "a1"), TestLiteral("Bob")),
                    Op.GT -> (TestColumn("distance", "b"), TestColumn("age", "a2")))

    val query =  getQuery(selectors, from, where)

    val validationResults = QueryValidator(query, tables)
    assert(validationResults.isRight)

    val totalNumberOfColumns = tables.map(t => t.columns.size()).sum
    val totalNumberOfRecords = (tables.size() * totalNumberOfColumns)
    val joinedTable = JoinTables(tables, query.from.toList)

    assert(joinedTable.rows.size == totalNumberOfRecords)
    assert(joinedTable.columns.size == totalNumberOfColumns)
    assert(joinedTable.rows.head == List("Alice", "20", "Alice", "20", "Q", "25"))

    val finalTable = Comparator(joinedTable, query.where.toList)

    assert(finalTable.rows.size == 6)

    val selectedColumns = ColumnsSelector(finalTable.columns, query.select.toList)

    assert(selectedColumns.size == selectors.size)
  }

  trait TestData2 {
    val from = Map("cities" -> "cities")

    val selectors = Map("name"->  TestColumn("name", "cities"), "country"->  TestColumn("country", "cities"),
      "population"->  TestColumn("population", "cities"))

    val where = Map(Op.EQ ->(TestColumn("name", "cities"), TestLiteral("Houston")),
                    Op.GT -> (TestColumn("population", "cities"), TestLiteral(new Integer(3000))))

    val table1 = generateTable(columns = Seq(("name", SqlType.STR), ("country", SqlType.STR), ("population", SqlType.INT)),
      rows = Seq(Seq("New York", "USA", new Integer(8537)),
                 Seq("Los Angeles", "USA", new Integer(3976)),
                 Seq("Houston", "USA", new Integer(2303))))
  }


  // Test with one table and multiple conditions
  it should "return filtered table with multiple conditions" in new TestData2 {
    val tables = getTables(Seq(table1))
    val query =  getQuery(selectors, from, where)

    val validationResults = QueryValidator(query, tables)
    assert(validationResults.isRight)

    val totalNumberOfColumns = tables.map(t => t.columns.size()).sum
    val totalNumberOfRecords = (tables.size() * totalNumberOfColumns)
    val joinedTable = JoinTables(tables, query.from.toList)

    assert(joinedTable.rows.size == totalNumberOfRecords)
    assert(joinedTable.columns.size == totalNumberOfColumns)
    assert(joinedTable.rows.head == List("NewYork", "USA", "8537"))

    val finalTable = Comparator(joinedTable, query.where.toList)

    assert(finalTable.rows.isEmpty)

    val selectedColumns = ColumnsSelector(finalTable.columns, query.select.toList)

    assert(selectedColumns.size == selectors.size)

  }

}
