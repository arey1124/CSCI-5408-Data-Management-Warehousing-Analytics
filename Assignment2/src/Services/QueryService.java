package Services;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryService {
    private static final String CREATE_PATTERN = "CREATE TABLE";
    private static final String SELECT_PATTERN = "SELECT";
    private static final String INSERT_PATTERN = "INSERT INTO";
    private static final String UPDATE_PATTERN = "UPDATE";
    private static final String DELETE_PATTERN = "DELETE FROM";

    private DataStoreService dataStoreService;
    private DataBuilderService dataBuilderService;
    private DataReaderService dataReaderService;
    private static final String TABLES_IDENTIFIER = "@@tables@@";
    private static final String DB_IDENTIFIER = "@@database@@";
    private static final String AUTH_FILE_PATH = "src/Database/user_credentials.txt";
    private static final String DB_SRC_PATH = "src/Database/";

    public QueryService(DataStoreService dataStoreService, DataBuilderService dataBuilderService, DataReaderService dataReaderService) {
        this.dataStoreService = dataStoreService;
        this.dataBuilderService = dataBuilderService;
        this.dataReaderService = dataReaderService;
    }

    public boolean execute(String userName, String query) {
        String dbPath = DB_SRC_PATH + getDBPath(userName);
        if (query.startsWith(CREATE_PATTERN)) {
            return executeCreate(dbPath, query);
        } else if (query.startsWith(SELECT_PATTERN)) {
            return executeSelect(dbPath, query);
        } else if (query.startsWith(INSERT_PATTERN)) {
            return executeInsert(dbPath, query);
        } else if (query.startsWith(UPDATE_PATTERN)) {
            executeUpdate();
        } else if (query.startsWith(DELETE_PATTERN)) {
            executeDelete();
        } else {
            System.out.println("Unsupported query type");
        }
        return false;
    }

    private boolean executeCreate(String dbPath, String query) {
        String tableName = parseTableName(query);
        try {
            File file = new File(dbPath + "/" + tableName + ".txt");
            if(file.exists()) {
                return false;
            }
            Map<String, String> fields = extractFields(query);
            Map<String, String> fieldAndKeys = extractFieldKeyMapping(query);
            dataStoreService.saveDataToFile(dbPath + "/table_desc.txt" ,dataBuilderService.buildTableData(tableName, fields, fieldAndKeys));
            dataStoreService.addTableHeadersTofile(file, fields);
        } catch (IOException e) {
            System.out.println("An error occurred while creating the file: " + e.getMessage());
            return false;
        }
        return true;
    }

    private boolean executeSelect(String dbPath, String query) {


        return false;
    }

    private boolean executeInsert(String dbPath, String query) {
        System.out.println("Inside insert");
        String tableName = parseTableName(query);
        File file = new File(dbPath + "/" + tableName + ".txt");
        if(!file.exists()) {
            System.out.println("File not exists");
            return false;
        }
        try {
            dataStoreService.addDataToTable(file, extractTableValues(query));
        } catch (IOException e) {
            System.out.println("An error occurred while inserting data: " + e.getMessage());
            return false;
        }
        return false;
    }

    private boolean executeUpdate() {
        return false;
    }

    private boolean executeDelete() {
        return false;
    }

    private static String parseTableName(String query) {
        String tableName = null;

        String[] keywords = { CREATE_PATTERN, INSERT_PATTERN, DELETE_PATTERN, UPDATE_PATTERN, SELECT_PATTERN };
        for (String keyword : keywords) {
            if (query.startsWith(keyword)) {
                String remainingQuery = query.substring(keyword.length()).trim();
                if (keyword.equals("SELECT")) {
                    tableName = extractTableNameFromSelect(query);
                } else {
                    tableName = extractTableName(remainingQuery);
                }
                break;
            }
        }

        return tableName;
    }

    private static String extractTableNameFromSelect(String query) {
        String tableName = null;
        Pattern pattern = Pattern.compile("FROM\\s+(\\w+)");
        Matcher matcher = pattern.matcher(query);
        if (matcher.find()) {
            tableName = matcher.group(1);
        }
        return tableName;
    }

    private static String extractTableName(String query) {
        String tableName = null;
        if (query.contains(" ")) {
            tableName = query.substring(0, query.indexOf(" "));
        } else {
            tableName = query;
        }
        return tableName;
    }

    private static Map<String, String> extractFields(String createQuery) {
        Map<String, String> fields = new LinkedHashMap<>();

        Pattern pattern = Pattern.compile("(?<=\\().*(?=\\))");
        Matcher matcher = pattern.matcher(createQuery);

        if (matcher.find()) {
            String fieldSection = matcher.group();
            String[] fieldDefinitions = fieldSection.split(",");

            for (String fieldDefinition : fieldDefinitions) {
                String[] fieldParts = fieldDefinition.trim().split("\\s+");
                String fieldName = fieldParts[0];
                String fieldType = fieldParts[1];
                if(fieldName.toUpperCase().trim().equals("FOREIGN") && fieldType.toUpperCase().trim().equals("KEY")) {
                    continue;
                }
                fields.put(fieldName, fieldType);
            }
        }

        return fields;
    }

    private static List<String> extractTableValues(String insertQuery) {
        List<String> values = new ArrayList<>();
        Pattern pattern = Pattern.compile("(?<=VALUES\\s*\\().*?(?=\\))");
        Matcher matcher = pattern.matcher(insertQuery);
        while (matcher.find()) {
            String value = matcher.group();
            String[] seperatedVal = value.split(",");
            for(String val : seperatedVal) {
                values.add(val.trim());
            }
        }
        return values;
    }

    private static Map<String, String> extractFieldKeyMapping(String createQuery) {
        Map<String, String> fieldKeyMap = new LinkedHashMap<>();

        Pattern pattern = Pattern.compile("(?<=\\().*(?=\\))");
        Matcher matcher = pattern.matcher(createQuery);

        if (matcher.find()) {
            String fieldSection = matcher.group();
            String[] fieldDefinitions = fieldSection.split(",");
            String keyType;
            for (String fieldDefinition : fieldDefinitions) {
                String[] fieldParts = fieldDefinition.trim().split("\\s+");
                String fieldName = fieldParts[0];
                keyType = null;
                if(fieldName.toUpperCase().trim().equals("FOREIGN")) {
                    Pattern fkPattern = Pattern.compile("FOREIGN KEY \\((\\w+)\\) REFERENCES (\\w+)\\((\\w+)\\)");
                    Matcher fkMatcher = fkPattern.matcher(createQuery);
                    if(fkMatcher.find()) {
                        fieldKeyMap.put(fkMatcher.group(1), "FOREIGN KEY "+ fkMatcher.group(2) + "(" +fkMatcher.group(3) +")");
                    }
                } else {
                    keyType = getKeyType(fieldParts);
                }
                if(keyType != null && !keyType.isEmpty()) {
                    fieldKeyMap.put(fieldName, keyType);
                }

            }
        }

        return fieldKeyMap;
    }

    private static String getKeyType(String[] fieldParts) {
        String keyType = "";

        for (int i = 2; i < fieldParts.length; i++) {
            String part = fieldParts[i].toUpperCase();
            if (part.equals("PRIMARY") || part.equals("FOREIGN") || part.equals("UNIQUE")) {
                keyType = fieldParts[i] + " " + fieldParts[i + 1];
                break;
            }
        }

        return keyType;
    }

    private String getDBPath (String userName) {
        String userData = dataReaderService.getUserData(AUTH_FILE_PATH, userName);
        String database = dataReaderService.extractValue(userData, DB_IDENTIFIER);
        if(database != null) {
            return userName + "." + database;
        }
        return null;
    }
}
