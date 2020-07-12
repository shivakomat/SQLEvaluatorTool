object Main extends App {

    val parsedConfig = AppArgumentsParser(args) match {
        case Left(error) => {
            System.err.println(error)
            System.err.println("Usage: COMMAND <table-folder> <sql-json-file> <output-file>")
            System.exit(1);
            None
        }
        case Right(config) => Some(config)
    }

    val config = parsedConfig.get

    println("Argument Selections :::::::::::::::::::::::")
    println("Output File : " + config.outputFile)
    println("SQL Json File : " + config.sqlJsonFile)
    println("Table Folder : " + config.tableFolder)




}
