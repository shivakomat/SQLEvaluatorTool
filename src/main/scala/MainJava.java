import com.fasterxml.jackson.core.JsonProcessingException;
import jsonsqlparser.Query;
import jsonsqlparser.Table;
import jsonsqlparser.TableDecl;
import utils.JacksonUtil;

import java.io.IOException;
import java.io.FileWriter;
import java.io.File;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public final class MainJava {
    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.err.println("Usage: COMMAND <table-folder> <sql-json-file> <output-file>");
            System.exit(1); return;
        }

        // user command line arguments parsing

        String tableFolder = args[0];
        String sqlJsonFile = args[1];
        String outputFile = args[2];


        // below building a query project

        Query query;
        try {
            query = JacksonUtil.readFromFile(sqlJsonFile, Query.class);
        } catch (JsonProcessingException ex) {
            System.err.println("Error loading \"" + sqlJsonFile + "\" as query JSON: " + ex.getMessage());
            System.exit(1); return;
        }

        // below loading tables

        ArrayList<Table> tables = new ArrayList<>();
        for (TableDecl tableDecl : query.from) {
            String tableSourcePath = tableFolder + File.separator + (tableDecl.source + ".table.json");
            Table table;
            try {
                table = JacksonUtil.readFromFile(tableSourcePath, Table.class);
            } catch (JsonProcessingException ex) {
                System.err.println("Error loading \"" + tableSourcePath + "\" as table JSON: " + ex.getMessage());
                System.exit(1); return;
            }
            tables.add(table);
        }

        // TODO: Actually evaluate query.



        // For now, just dump the input back out.
        try (FileWriter out = new FileWriter(outputFile)) {
            JacksonUtil.writeIndented(out, query);

            for (Table table : tables) {
                writeTable(out, table);
            }
        }
    }

    public static void writeTable(Writer out, Table table) throws IOException {
        out.write("[\n");

        out.write("    ");
        JacksonUtil.write(out, table.columns);

        for (List<Object> row : table.rows) {
            out.write(",\n    ");
            JacksonUtil.write(out, row);
        }

        out.write("\n]\n");
    }
}
