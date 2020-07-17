import jsonsqlmodel.Condition.Op
import jsonsqlmodel.SqlType
import org.scalatest.FlatSpec
import sqlevaluator.validator.IncompatibleConditionDataTypes
import testdatabuilders.{TestQueryFixtureBuilder, TestTableFixtureBuilder}

class TestIncompatibleConditionDataTypes extends FlatSpec with TestQueryFixtureBuilder with TestTableFixtureBuilder {

  trait TestDataWithCompatibleConditions {
    val from = Map("a1" -> "a", "a2" -> "a", "b" -> "b")

    val select = Map("a1_name"->  TestColumn("name", "a1"),
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
    val where = Map(Op.NE ->(TestColumn("name", "a1"), TestLiteral("Bob")),
      Op.GT -> (TestColumn("distance", "b"), TestColumn("age", "a2")))

    val query =  getQuery(select, from, where)

  }

  "IncompatibleConditionDataTypes" should "return the query back with the Right side evaluation" in new TestDataWithCompatibleConditions {
    val result = IncompatibleConditionDataTypes(query, tables)
    assert(result.isRight)
  }

  trait TestDataWithIncompatibleOperatorConditions {
    val from = Map("countries" -> "countries", "cities" -> "cities")

    val selectors = Map("name"->  TestColumn("name", null), "country"->  TestColumn("country", null),
      "population"->  TestColumn("population", null))

    val where = Map(Op.NE ->(TestColumn("name", null), TestColumn("population", null)))

    val table1 = generateTable(columns = Seq(("name", SqlType.STR), ("country", SqlType.STR), ("population", SqlType.INT)),
      rows = Seq(Seq("New York", "USA", new Integer(8537))))

    val table2 = generateTable(columns = Seq(("name", SqlType.STR), ("capital", SqlType.STR)),
      rows = Seq(Seq("USA", "Washington DC")))

    val tables = getTables(Seq(table1, table2))

    val query =  getQuery(selectors, from, where)
  }

  it should "return a left evaluation with an incompatible operator between types str and int" in new TestDataWithIncompatibleOperatorConditions {
    val result = IncompatibleConditionDataTypes(query, tables)
    assert(result.isLeft)
  }

  trait TestDataWithIncompatibleOperatorWithLiteral {
    val from = Map("countries" -> "countries", "cities" -> "cities")

    val selectors = Map("name"->  TestColumn("name", null), "country"->  TestColumn("country", null),
      "population"->  TestColumn("population", null))

    val where = Map(Op.NE ->(TestColumn("name", null), TestLiteral(new Integer(20))))

    val table1 = generateTable(columns = Seq(("name", SqlType.STR), ("country", SqlType.STR), ("population", SqlType.INT)),
      rows = Seq(Seq("New York", "USA", new Integer(8537))))

    val table2 = generateTable(columns = Seq(("name", SqlType.STR), ("capital", SqlType.STR)),
      rows = Seq(Seq("USA", "Washington DC")))

    val tables = getTables(Seq(table1, table2))

    val query =  getQuery(selectors, from, where)
  }

  it should "return a left evaluation with an incompatible operator between types int and literal string value" in new TestDataWithIncompatibleOperatorWithLiteral {
    val result = IncompatibleConditionDataTypes(query, tables)
    assert(result.isLeft)
  }

  trait TestDataWithCompatibleOperatorWithLiteral {
    val from = Map("countries" -> "countries", "cities" -> "cities")

    val selectors = Map("name"->  TestColumn("name", null), "country"->  TestColumn("country", null),
      "population"->  TestColumn("population", null))

    val where = Map(Op.NE ->(TestColumn("name", null), TestLiteral("New York")))

    val table1 = generateTable(columns = Seq(("name", SqlType.STR), ("country", SqlType.STR), ("population", SqlType.INT)),
      rows = Seq(Seq("New York", "USA", new Integer(8537))))

    val table2 = generateTable(columns = Seq(("name", SqlType.STR), ("capital", SqlType.STR)),
      rows = Seq(Seq("USA", "Washington DC")))

    val tables = getTables(Seq(table1, table2))

    val query =  getQuery(selectors, from, where)
  }

  it should "return a right evaluation with an compatible operator between column type and literal string value" in new TestDataWithCompatibleOperatorWithLiteral {
    val result = IncompatibleConditionDataTypes(query, tables)
    assert(result.isRight)
  }

  trait TestDataWithCompatibleOperatorColumns {
    val from = Map("countries" -> "countries", "cities" -> "cities")

    val selectors = Map("name"->  TestColumn("name", null), "country"->  TestColumn("country", null),
      "population"->  TestColumn("population", null))

    val where = Map(Op.NE ->(TestColumn("name", "cities"), TestColumn("capital", null)))

    val table1 = generateTable(columns = Seq(("name", SqlType.STR), ("country", SqlType.STR), ("population", SqlType.INT)),
      rows = Seq(Seq("New York", "USA", new Integer(8537))))

    val table2 = generateTable(columns = Seq(("name", SqlType.STR), ("capital", SqlType.STR)),
      rows = Seq(Seq("USA", "Washington DC")))

    val tables = getTables(Seq(table1, table2))

    val query =  getQuery(selectors, from, where)
  }

  it should "return a right evaluation with compatible operator condition between two string columns" in new TestDataWithCompatibleOperatorColumns {
    val result = IncompatibleConditionDataTypes(query, tables)
    assert(result.isRight)
  }

  trait TestDataForIncompatibleOperator {
    val from = Map("countries" -> "countries", "cities" -> "cities")

    val selectors = Map("name"->  TestColumn("name", null), "country"->  TestColumn("country", null),
      "population"->  TestColumn("population", null))

    val where = Map(Op.GT ->(TestColumn("name", "cities"), TestColumn("capital", null)))

    val table1 = generateTable(columns = Seq(("name", SqlType.STR), ("country", SqlType.STR), ("population", SqlType.INT)),
      rows = Seq(Seq("New York", "USA", new Integer(8537))))

    val table2 = generateTable(columns = Seq(("name", SqlType.STR), ("capital", SqlType.STR)),
      rows = Seq(Seq("USA", "Washington DC")))

    val tables = getTables(Seq(table1, table2))

    val query =  getQuery(selectors, from, where)
  }

  it should "return a left evaluation due to the operator GT is trying to act on two str columns" in new TestDataForIncompatibleOperator {
    val result = IncompatibleConditionDataTypes(query, tables)
    assert(result.isLeft)
  }

  trait TestDataWithMultipleConditions {
    val from = Map("countries" -> "countries", "cities" -> "cities")

    val selectors = Map("name"->  TestColumn("name", null), "country"->  TestColumn("country", null),
      "population"->  TestColumn("population", null))

    val where = Map(Op.GT ->(TestColumn("name", "cities"), TestColumn("capital", null)),
      Op.EQ ->(TestColumn("name", "cities"), TestColumn("capital", null)))

    val table1 = generateTable(columns = Seq(("name", SqlType.STR), ("country", SqlType.STR), ("population", SqlType.INT)),
      rows = Seq(Seq("New York", "USA", new Integer(8537))))

    val table2 = generateTable(columns = Seq(("name", SqlType.STR), ("capital", SqlType.STR)),
      rows = Seq(Seq("USA", "Washington DC")))

    val tables = getTables(Seq(table1, table2))

    val query =  getQuery(selectors, from, where)
  }

  it should "return a left evaluation due to one of the in valid operator GT use with two str columns" in new TestDataWithMultipleConditions {
    val result = IncompatibleConditionDataTypes(query, tables)
    assert(result.isLeft)
  }

  trait TestDataWithMultipleNumericConditions {
    val from = Map("countries" -> "countries", "cities" -> "cities")

    val selectors = Map("name"->  TestColumn("name", null), "country"->  TestColumn("country", null),
      "population"->  TestColumn("population", null))

    val where = Map(Op.GT ->(TestColumn("population", null), TestLiteral(new Integer(1000))),
      Op.EQ ->(TestColumn("name", "cities"), TestColumn("capital", null)), Op.EQ ->(TestColumn("population", null), TestLiteral(new Integer(20000))))

    val table1 = generateTable(columns = Seq(("name", SqlType.STR), ("country", SqlType.STR), ("population", SqlType.INT)),
      rows = Seq(Seq("New York", "USA", new Integer(8537))))

    val table2 = generateTable(columns = Seq(("name", SqlType.STR), ("capital", SqlType.STR)),
      rows = Seq(Seq("USA", "Washington DC")))

    val tables = getTables(Seq(table1, table2))

    val query =  getQuery(selectors, from, where)
  }

  it should "return a right evaluation since all the condition have compatible operator and columns use" in new TestDataWithMultipleNumericConditions {
    val result = IncompatibleConditionDataTypes(query, tables)
    assert(result.isRight)
  }

}
