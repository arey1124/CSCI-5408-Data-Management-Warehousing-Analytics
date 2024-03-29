package Services;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataReaderService {

    private DBHelper dbHelper;
    private static final String DB_IDENTIFIER = "@@database@@";
    private static final String AUTH_FILE_PATH = "src/Database/user_credentials.txt";
    private static final String DB_SRC_PATH = "src/Database/";
    public DataReaderService() {
        this.dbHelper = new DBHelper();
    }

    /**
     * Gets the user data from the user_credentials based on the identifier
     * @param filePath
     * @param identifier is the current logged-in username
     * @return
     */
    public String getUserData(String filePath, String identifier) {
        StringBuilder userData = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean foundItem = false;

            while ((line = reader.readLine()) != null) {
                if (line.trim().equals("@@" + identifier + "@@")) {
                    if(foundItem) {
                        break;
                    } else{
                        foundItem = true;
                    }
                    continue;
                }

                if (foundItem) {
                    userData.append(line.trim());
                }
            }

            if (!foundItem) {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return userData.toString();
    }

    /**
     * Parse the data and extract identifier value based on the identifier from the provided data
     * @param data
     * @param identifier
     * @return
     */
    public String extractValue(String data, String identifier) {
        int startIndex = data.indexOf(identifier) + identifier.length();
        int endIndex = data.indexOf(identifier, startIndex);

        if (startIndex != -1 && endIndex != -1) {
            return data.substring(startIndex, endIndex);
        } else {
            return null;
        }
    }

    /**
     * Read table records based on the query provided
     * @param dbPath is the database file path where the data is stored
     * @param tableName
     * @param query
     * @return
     */
    public List<List<String>> getTableRecordsFromQuery (String dbPath, String tableName, String query) {
        try {
            List<String> columns = new ArrayList<>();
            List<List<String>> rows = new ArrayList<>();

            BufferedReader reader = new BufferedReader(new FileReader(dbPath + "/" + tableName + ".txt"));
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    columns = Arrays.asList(line.split("@@"));
                    isFirstLine = false;
                } else {
                    rows.add(Arrays.asList(line.split("@@")));
                }
            }

            if(query.contains("SELECT *")) {
                rows.add(0, columns);
                return rows;
            } else {
                List<Integer> selectedColumnIndexes = new ArrayList<>();
                List<String> selectedColumnNames = new ArrayList<>();
                String[] selectedColumns = query.split("SELECT ")[1].split(" FROM ")[0].trim().split(", ");

                for (String column : selectedColumns) {
                    selectedColumnIndexes.add(columns.indexOf(column));
                    selectedColumnNames.add(column);
                }

                List<List<String>> filteredRows = new ArrayList<>();

                for (List<String> row : rows) {
                    List<String> selectedRowValues = new ArrayList<>();

                    for (int index : selectedColumnIndexes) {
                        selectedRowValues.add(row.get(index));
                    }
                    filteredRows.add(selectedRowValues);
                }

                filteredRows.add(0, selectedColumnNames);
                return filteredRows;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns the current logged-in user's tables definitions
     * @param userName
     * @return
     */
    public String getTableDesc (String userName) {
        String userData = this.getUserData(AUTH_FILE_PATH, userName);
        String database = this.extractValue(userData, DB_IDENTIFIER);

        String filePath = DB_SRC_PATH + dbHelper.getDBPath(userName, database) + "/table_desc.txt";
        try {
            Path path = Paths.get(filePath);
            byte[] bytes = Files.readAllBytes(path);
            return new String(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
 }
