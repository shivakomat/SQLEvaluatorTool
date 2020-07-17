package sqlevaluator.validator

import jsonsqlmodel._

sealed class QueryValidator(query: Query, tables: List[Table]) {

  def run(): Either[String, Query] =
    for {
      validatedForAmbiguity <- AmbiguousColumnsValidation(query, tables)
      inCompatibleTypes <- IncompatibleConditionDataTypes(query, tables)
    } yield inCompatibleTypes

}

object QueryValidator {
  def apply(query: Query, tables: List[Table]): Either[String, Query] =
    new QueryValidator(query, tables).run()
}