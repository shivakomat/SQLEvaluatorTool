package sqlevaluator

import jsonsqlparser.Condition.Op
import jsonsqlparser.Term.{Column, Literal}
import jsonsqlparser._
import utils.TablesLoader
import scala.collection.JavaConversions._

object Evaluator extends App {
  """{
    "select": [
    {"name": "a1_name", "source": {"name": "name", "table": "a1"}},
    {"name": "age", "source": {"name": "age", "table": "a1"}},
    {"name": "a2_name", "source": {"name": "name", "table": "a2"}},
    {"name": "distance", "source": {"name": "distance", "table": "b"}}
    ],
    "from": [
    {"name": "a1", "source": "a"},
    {"name": "a2", "source": "a"},
    {"name": "b", "source": "b"}
    ],
    "where": [
    {
      "op": ">",
      "left": {"column": {"name": "distance", "table": null}},
      "right": {"column": {"name": "age", "table": "a2"}}
    },
    {
      "op": "!=",
      "left": {"column": {"name": "name", "table": "a1"}},
      "right": {"literal": "Bob"}
    }
    ]
  }"""
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
  val tables = TablesLoader(query.from, "/Users/shivakomatreddy/IdeaProjects/AirTableSQLEvaluator/src/main/scala/sqlevaluator/examples")

  val joinedTable = JoinTables(tables.toList, froms.toList)
//  val filteredTable = SQLOperations(joinedTable).applyWhereClauses(whereConditions.toList)
//  filteredTable.rows.forEach(t => println(t))

  """{
    "select": [
        {"name": "name", "source": {"name": "name", "table": null}},
        {"name": "population", "source": {"name": "population", "table": null}}
    ],
    "from": [
        {"name": "cities", "source": "cities"}
    ],
    "where": [
        {
            "op": "=",
            "left": {"column": {"name": "country", "table": null}},
            "right": {"literal": "Japan"}
        },
        {
            "op": ">",
            "left": {"column": {"name": "population", "table": null}},
            "right": {"literal": 8000}
        }
    ]
}"""

  val selectors2 = new java.util.ArrayList[Selector]()
  val s21 = new Selector("name", new ColumnRef("name", "null"))
  val s22 = new Selector("population", new ColumnRef("population", "null"))

  selectors2.add(s21)
  selectors2.add(s22)

  val f21  = new TableDecl("cities", "cities")
  val froms2 =  new java.util.ArrayList[TableDecl]()
  froms2.add(f21)


  val whereConditions2 = new java.util.ArrayList[Condition]()
  val condition21 = new Condition(Op.GT, new Column(new ColumnRef("population", "null")), new Literal(8000))
  val condition22 = new Condition(Op.EQ, new Column(new ColumnRef("country", "null")), new Literal("Japan"))
  whereConditions2.add(condition21)
  whereConditions2.add(condition22)


  val query2 =  new Query(selectors2, froms2, whereConditions2)
  val tables2 = TablesLoader(query2.from, "/Users/shivakomatreddy/IdeaProjects/AirTableSQLEvaluator/src/main/scala/sqlevaluator/examples")
  val joinedTable2 = JoinTables(tables2.toList, froms2.toList)

//  joinedTable2.rows.foreach(println)


  val filteredTable2 = Comparator(joinedTable2, whereConditions2.toList)
//  filteredTable2.rows.forEach(t => println(t))


  """{
    |    "select": [
    |        {"name": "name", "source": {"name": "name", "table": "others"}}
    |    ],
    |    "from": [
    |        {"name": "countries", "source": "countries"},
    |        {"name": "capitals", "source": "cities"},
    |        {"name": "others", "source": "cities"}
    |    ],
    |    "where": [
    |        {
    |            "op": "=",
    |            "left": {"column": {"name": "capital", "table": "countries"}},
    |            "right": {"column": {"name": "name", "table": "capitals"}}
    |        },
    |        {
    |            "op": "=",
    |            "left": {"column": {"name": "name", "table": "countries"}},
    |            "right": {"column": {"name": "country", "table": "others"}}
    |        },
    |        {
    |            "op": ">",
    |            "left": {"column": {"name": "population", "table": "others"}},
    |            "right": {"column": {"name": "population", "table": "capitals"}}
    |        }
    |    ]
    |}""".stripMargin

  val selectors3 = new java.util.ArrayList[Selector]()
  val s31 = new Selector("name", new ColumnRef("name", "others"))

  selectors3.add(s31)

  val f31  = new TableDecl("countries", "countries")
  val f32  = new TableDecl("capitals", "cities")
  val f33  = new TableDecl("others", "cities")

  val froms3 =  new java.util.ArrayList[TableDecl]()
  froms3.add(f31)
  froms3.add(f32)
  froms3.add(f33)

  val whereConditions3 = new java.util.ArrayList[Condition]()
  val condition31 = new Condition(Op.EQ, new Column(new ColumnRef("capital", "countries")), new Column(new ColumnRef("name", "capitals")))
  val condition32 = new Condition(Op.EQ, new Column(new ColumnRef("name", "countries")), new Column(new ColumnRef("country", "others")))
  val condition33 = new Condition(Op.GT, new Column(new ColumnRef("population", "others")), new Column(new ColumnRef("population", "capitals")))

  whereConditions3.add(condition31)
  whereConditions3.add(condition32)
  whereConditions3.add(condition33)

  val query3 =  new Query(selectors3, froms3, whereConditions3)
  val tables3 = TablesLoader(query3.from, "/Users/shivakomatreddy/IdeaProjects/AirTableSQLEvaluator/src/main/scala/sqlevaluator/examples")
  val joinedTable3 = JoinTables(tables3.toList, froms3.toList)

//  joinedTable3.rows.foreach(println)

  println("----------->>>>>>")
  println("----------->>>>>>")
  println("----------->>>>>>")

  val filteredTable3 = Comparator(joinedTable3, whereConditions3.toList)
  val pos = filteredTable3.columns.zipWithIndex
    .filter(col => col._1.srcTable.name == "others" && col._1.columnName.name == "name").head._2

  println(filteredTable3.columns.map(e => (e.srcTable.name + "." + e.columnName.name)).mkString(","))
//  filteredTable3.rows.forEach(t => println(t))
  filteredTable3.rows.forEach(t => println(t(pos)))

}