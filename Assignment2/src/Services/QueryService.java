package Services;

import java.io.*;
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
    private DBHelper dbHelper;

    public QueryService(DataStoreService dataStoreService, DataBuilderService dataBuilderService, DataReaderService dataReaderService) {
        this.dataStoreService = dataStoreService;
        this.dataBuilderService = dataBuilderService;
        this.dataReaderService = dataReaderService;
        this.dbHelper = new DBHelper();
    }

    public boolean execute(String userName, String query) {
        String userData = dataReaderService.getUserData(AUTH_FILE_PATH, userName);
        String database = dataReaderService.extractValue(userData, DB_IDENTIFIER);

        String dbPath = DB_SRC_PATH + dbHelper.getDBPath(userName, database);

        if (query.startsWith(CREATE_PATTERN)) {
            return executeCreate(dbPath, query);
        } else if (query.startsWith(SELECT_PATTERN)) {
            return executeSelect(dbPath, query);
        } else if (query.startsWith(INSERT_PATTERN)) {
            return executeInsert(dbPath, query);
        } else if (query.startsWith(UPDATE_PATTERN)) {
            return executeUpdate(dbPath, query);
        } else if (query.startsWith(DELETE_PATTERN)) {
            return executeDelete(dbPath, query);
        } else {
            System.out.println("Unsupported query type");
        }
        return false;
    }

    private boolean executeCreate(String dbPath, String query) {
        String tableName = dbHelper.parseTableName(query);
        try {
            File file = new File(dbPath + "/" + tableName + ".txt");
            if(file.exists()) {
                return false;
            }
            Map<String, String> fields = dbHelper.extractFields(query);
            Map<String, String> fieldAndKeys = dbHelper.extractFieldKeyMapping(query);
            dataStoreService.saveDataToFile(dbPath + "/table_desc.txt" ,dataBuilderService.buildTableData(tableName, fields, fieldAndKeys));
            dataStoreService.addTableHeadersTofile(file, fields);
        } catch (IOException e) {
            System.out.println("An error occurred while creating the file: " + e.getMessage());
            return false;
        }
        return true;
    }

    private boolean executeSelect(String dbPath, String query) {
        String tableName = dbHelper.parseTableName(query);
        if(tableName == null || tableName.isEmpty()) {
            System.out.println("Table does not exists");
            return false;
        }

        List<List<String>> data = dataReaderService.getTableRecordsFromQuery(dbPath, tableName, query);
        dbHelper.printTable(data);
        return false;
    }

    private boolean executeInsert(String dbPath, String query) {
        String tableName = dbHelper.parseTableName(query);
        File file = new File(dbPath + "/" + tableName + ".txt");
        if(!file.exists()) {
            System.out.println("File not exists");
            return false;
        }
        try {
            dataStoreService.addDataToTable(file, dbHelper.extractTableValues(query));
        } catch (IOException e) {
            System.out.println("An error occurred while inserting data: " + e.getMessage());
            return false;
        }
        return false;
    }

    private boolean executeUpdate(String dbPath, String query) {
        String tableName = dbHelper.parseTableName(query);
        File file = new File(dbPath + "/" + tableName + ".txt");
        if(!file.exists()) {
            System.out.println("File not exists");
            return false;
        }
        // Define the regular expression pattern
        String patternString = "UPDATE\\s+(\\w+)\\s+SET\\s+(\\w+)\\s+=\\s+('[^']*'|\\d+).*WHERE\\s+(\\w+)\\s+=\\s+(\\d+)";
        Pattern pattern = Pattern.compile(patternString);

        // Match the pattern against the query
        Matcher matcher = pattern.matcher(query);

        if (matcher.matches()) {
            // Extract the relevant data
            String columnName = matcher.group(2);
            String newValue = matcher.group(3);
            String conditionColumn = matcher.group(4);
            String conditionValue = matcher.group(5);
            dataStoreService.updateTableData(file, columnName, newValue, conditionColumn, conditionValue);
        } else {
            System.out.println("Invalid update query format.");
            return false;
        }
        return true;
    }

    private boolean executeDelete(String dbPath, String query) {

        // Match the delete query using regular expressions
        Pattern pattern = Pattern.compile("DELETE FROM (\\w+)\\s* WHERE (\\w+)\\s*=\\s*(\\w+)");
        Matcher matcher = pattern.matcher(query);

        if (matcher.matches()) {
            String tableName = matcher.group(1);
            String columnName = matcher.group(2);
            String columnValue = matcher.group(3);
            File file = new File(dbPath + "/" + tableName + ".txt");
            if(!file.exists()) {
                System.out.println("File not exists");
                return false;
            }
            dataStoreService.removeTableData(file, columnName, columnValue);
            return true;
        }
        return false;
    }
}
