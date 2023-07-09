package Services;

import java.util.HashMap;
import java.util.Map;

public class DataBuilderService {
    private static final String DELIMITER = "@@";
    public String buildUserData(String username, String password, String question, String answer) {
        StringBuilder authPartBuilder = new StringBuilder();
        authPartBuilder.append(DELIMITER).append(username).append(DELIMITER).append(System.lineSeparator())
                .append("\t@@auth@@").append(System.lineSeparator())
                .append("\t\t@@password@@").append(password).append("@@password@@").append(System.lineSeparator())
                .append("\t\t@@question@@").append(question).append("@@question@@").append(System.lineSeparator())
                .append("\t\t@@answer@@").append(answer).append("@@answer@@").append(System.lineSeparator())
                .append("\t@@auth@@").append(System.lineSeparator())
                .append(DELIMITER).append(username).append(DELIMITER).append(System.lineSeparator());

        return authPartBuilder.toString();
    }

    public String buildDatabaseData(String dbName) {
        StringBuilder dbBuilder = new StringBuilder();
        dbBuilder
                .append("\t@@database@@").append(dbName).append("@@database@@").append(System.lineSeparator());
        return dbBuilder.toString();
    }

    public String buildTableData(String tableName, Map<String, String> fields, Map<String, String> keys) {
        StringBuilder tableBuilder = new StringBuilder();
        tableBuilder.append(DELIMITER).append("table").append(DELIMITER).append(System.lineSeparator());
        tableBuilder.append("\t").append(DELIMITER).append(tableName).append(DELIMITER).append(System.lineSeparator())
                .append("\t\t@@fields@@").append(System.lineSeparator());
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            tableBuilder.append("\t\t\t").append(DELIMITER).append(entry.getKey() + "[" + entry.getValue() + "]").append(DELIMITER).append(System.lineSeparator());
        }
        tableBuilder.append("\t\t@@fields@@").append(System.lineSeparator());

        tableBuilder.append("\t\t@@keys@@").append(System.lineSeparator());
        for (Map.Entry<String, String> entry : keys.entrySet()) {
            tableBuilder.append("\t\t\t").append(DELIMITER).append(entry.getKey() + "[" + entry.getValue() + "]").append(DELIMITER).append(System.lineSeparator());
        }
        tableBuilder.append("\t\t@@keys@@").append(System.lineSeparator());

        tableBuilder.append("\t").append(DELIMITER).append(tableName).append(DELIMITER).append(System.lineSeparator());
        tableBuilder.append(DELIMITER).append("table").append(DELIMITER).append(System.lineSeparator());
        return tableBuilder.toString();
    }
}
