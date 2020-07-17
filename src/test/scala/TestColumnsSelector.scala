import jsonsqlmodel.SqlType
import org.scalatest.FlatSpec
import testdatabuilders.{TestQueryFixtureBuilder, TestTableFixtureBuilder}

class TestColumnsSelector extends FlatSpec with TestQueryFixtureBuilder with TestTableFixtureBuilder {

    trait TestData1 {
      val selectors = Map("a1_name"->  TestColumn("name", "a1"),
        "age"->  TestColumn("age", "a2"),
        "a2_name"->  TestColumn("name", "a2"),
        "distance"->  TestColumn("distance", "b"))

      val columns = getColumns(Seq(("name", SqlType.STR), ("age", SqlType.INT)))
    }

    "column selector" should "select the columns asked by the selector with a given set of columns from a table" in new TestData1 {


    }

}
