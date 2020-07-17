package config

object AppArgumentsParser {

  def apply(args: Seq[String]): Either[String, Config] =
    if (args.isEmpty) Left("No arguments added")
    else if (args.size < 3) Left("Not enough arguments set")
    else {
      new scopt.OptionParser[Config]("SQL Evaluator") {
        head("---------------SQL Evaluator App------------------------------")

        // Table Folder
        opt[String]('t', "tableFolder") required() action { (i, config) =>
          config.copy(tableFolder = i)
        } text ("Invalid tables folder path")

        // SQL Json File
        opt[String]('i', "sqlJsonFile") required() action { (i, config) =>
          config.copy(sqlJsonFile = i)
        } text ("invalid sql json file path")

        // Output File
        opt[String]('o', "outputFilePath") required() action { (i, config) =>
          config.copy(outputFile = i)
        } text ("invalid output file path")
      }
        .parse(args, Config()) match {
        case Some(config) => Right(config)
        case None => Left("Failed to parse arguments")
      }
    }

}
