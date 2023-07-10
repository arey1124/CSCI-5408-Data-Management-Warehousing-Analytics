package Services;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DBHelper {
    public static final String CREATE_PATTERN = "CREATE TABLE";
    public static final String SELECT_PATTERN = "SELECT";
    public static final String INSERT_PATTERN = "INSERT INTO";
    public static final String UPDATE_PATTERN = "UPDATE";
    public static final String DELETE_PATTERN = "DELETE FROM";
    public static String parseTableName(String query) {
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

    public static String extractTableNameFromSelect(String query) {
        String tableName = null;
        Pattern pattern = Pattern.compile("FROM\\s+(\\w+)");
        Matcher matcher = pattern.matcher(query);
        if (matcher.find()) {
            tableName = matcher.group(1);
        }
        return tableName;
    }

    public static String extractTableName(String query) {
        String tableName = null;
        if (query.contains(" ")) {
            tableName = query.substring(0, query.indexOf(" "));
        } else {
            tableName = query;
        }
        return tableName;
    }

    public static Map<String, String> extractFields(String createQuery) {
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

    public static List<String> extractTableValues(String insertQuery) {
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

    public static Map<String, String> extractFieldKeyMapping(String createQuery) {
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

    public static String getKeyType(String[] fieldParts) {
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

    public String getDBPath (String userName, String database) {
        if(database != null) {
            return userName + "." + database;
        }
        return null;
    }

    public static void printTable(List<List<String>> data) {
        if (data.isEmpty()) {
            System.out.println("No data available.");
            return;
        }

        int numColumns = data.get(0).size();
        int[] columnWidths = new int[numColumns];

        // Find the maximum width for each column
        for (List<String> row : data) {
            for (int i = 0; i < numColumns; i++) {
                int length = row.get(i).length();
                if (length > columnWidths[i]) {
                    columnWidths[i] = length;
                }
            }
        }

        // Print the top border
        printBorder(columnWidths);

        // Print the header row
        printRow(data.get(0), columnWidths);

        // Print the header-row separator
        printSeparator(columnWidths);

        // Print the data rows
        for (int rowIndex = 1; rowIndex < data.size(); rowIndex++) {
            printRow(data.get(rowIndex), columnWidths);
        }

        // Print the bottom border
        printBorder(columnWidths);
    }

    public static void printBorder(int[] columnWidths) {
        System.out.print("┌");
        for (int width : columnWidths) {
            System.out.print(repeatChar('─', width + 2));
            System.out.print("┬");
        }
        System.out.println();
    }

    public static void printRow(List<String> row, int[] columnWidths) {
        System.out.print("│");
        for (int i = 0; i < row.size(); i++) {
            String value = row.get(i);
            int width = columnWidths[i];
            System.out.print(" " + padString(value, width) + " ");
            System.out.print("│");
        }
        System.out.println();
    }

    public static void printSeparator(int[] columnWidths) {
        System.out.print("├");
        for (int width : columnWidths) {
            System.out.print(repeatChar('─', width + 2));
            System.out.print("┼");
        }
        System.out.println();
    }

    public static String repeatChar(char c, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(c);
        }
        return sb.toString();
    }

    public static String padString(String str, int length) {
        return String.format("%-" + length + "s", str);
    }
}
