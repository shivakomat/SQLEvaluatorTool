object AppArgumentsParser {

  def apply(args: Seq[String]): Either[String, Config] = {
    if (args.isEmpty) Left("No arguments added")
    else if (args.size < 3) Left("Not enough arguments set")
    else {
      new scopt.OptionParser[Config]("SQL Evaluator") {
        head("---------------SQL Evaluator App------------------------------")

        // Table Folder
        opt[String]('t', "<table-folder>") required() action { (i, config) =>
          config.copy(tableFolder = i)
        } text ("members data set input path")

        // SQL Json File
        opt[String]('i', "<sql-json-file>") required() action { (i, config) =>
          config.copy(sqlJsonFile = i)
        } text ("invalid members output path")

        // Output File
        opt[String]('o', "<output-file>") required() action { (i, config) =>
          config.copy(outputFile = i)
        } text ("deleted records output path")
      }
        .parse(args, Config()) match {
        case Some(config) => Right(config)
        case None => Left("Failed to parse arguments")
      }
    }
  }
}