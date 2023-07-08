package Services;

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
}
