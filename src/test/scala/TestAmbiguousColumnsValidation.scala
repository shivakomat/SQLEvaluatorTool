import jsonsqlmodel.Condition.Op
import jsonsqlmodel.SqlType
import org.scalatest.FlatSpec
import sqlevaluator.validator.AmbiguousColumnsValidation
import testdatabuilders.{TestQueryFixtureBuilder, TestTableFixtureBuilder}

class TestAmbiguousColumnsValidation extends FlatSpec with TestQueryFixtureBuilder with TestTableFixtureBuilder {

  trait TestDataWithNoIssues {
    val from = Map("a1" -> "a", "a2" -> "a", "b" -> "b")

    val selectors = Map("a1_name"->  TestColumn("name", "a1"),
      "age"->  TestColumn("age", "a2"),
      "a2_name"->  TestColumn("name", "a2"),
      "distance"->  TestColumn("distance", "b"))


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

    val tables = getTables(Seq(table1, table2, table3))
    val query =  getQuery(selectors, from, Map.empty[Op, (TestColumn, TCol)])
  }


  "AmbiguousColumnsValidation" should "return query back with out any errors" in new TestDataWithNoIssues {
     val result = AmbiguousColumnsValidation(query, tables)
     assert(result.isRight)
     assert(result.right.get.select.size() == 4)
  }

  trait TestDataWithAmbiguousColumns {
    val from = Map("countries" -> "countries", "cities" -> "cities")

    val selectors = Map("name"->  TestColumn("name", null), "country"->  TestColumn("country", null),
                        "population"->  TestColumn("population", null))

    val table1 = generateTable(columns = Seq(("name", SqlType.STR), ("country", SqlType.STR), ("population", SqlType.INT)),
                                rows = Seq(Seq("New York", "USA", new Integer(8537))))

    val table2 = generateTable(columns = Seq(("name", SqlType.STR), ("capital", SqlType.STR)),
                                rows = Seq(Seq("USA", "Washington DC")))

    val tables = getTables(Seq(table1, table2))
    val query =  getQuery(selectors, from, Map.empty[Op, (TestColumn, TCol)])
  }

  it should "return error message wrapped by left" in new TestDataWithAmbiguousColumns {
    val result = AmbiguousColumnsValidation(query, tables)
    assert(result.isLeft)
    assert(result.left.get == "ERROR: Column reference \"name\" are ambiguous; present in multiple tables: countries,cities.")
  }

  trait TestDataWithMultipleOfSameColumnButMappedRight {
    val from = Map("countries" -> "countries", "cities" -> "cities")

    val selectors = Map("name"->  TestColumn("name", "cities"), "country"->  TestColumn("country", null),
      "population"->  TestColumn("population", null))

    val table1 = generateTable(columns = Seq(("name", SqlType.STR), ("country", SqlType.STR), ("population", SqlType.INT)),
      rows = Seq(Seq("New York", "USA", new Integer(8537))))

    val table2 = generateTable(columns = Seq(("name", SqlType.STR), ("capital", SqlType.STR)),
      rows = Seq(Seq("USA", "Washington DC")))

    val tables = getTables(Seq(table1, table2))
    val query =  getQuery(selectors, from, Map.empty[Op, (TestColumn, TCol)])
  }

  it should "return no error messages and return a right evaluation" in new TestDataWithMultipleOfSameColumnButMappedRight {
    val result = AmbiguousColumnsValidation(query, tables)
    assert(result.isRight)
    assert(result.right.get.select.size == selectors.size)
  }

}
