package Services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ERDiagramGenerator {

    /**
     * Generate ER Diagram based on the data provided for the tables
     * @param data
     */
    public static void generateERDiagram(String data) {
        String[] tables = data.split("@@table@@");

        // Create a map to store table relationships
        Map<String, List<String>> relationships = new HashMap<>();
        Map<String, Map<String, String>> keys = new HashMap<>();
        Map<String, String> tableKeys;

        // Iterate over each table
        for (String table : tables) {
            if (!table.trim().isEmpty()) {
                String[] lines = table.split("\n");

                // Extract the table name
                String tableName = lines[1].trim().replace("@@", "");

                // Extract the fields
                List<String> fields = new ArrayList<>();
                tableKeys = new HashMap<>();
                boolean fieldSection = false, keySection = false;
                for (String line : lines) {
                    if (line.trim().equals("@@fields@@")) {
                        fieldSection = true;
                        keySection = false;
                    } else if (line.trim().equals("@@keys@@")) {
                        fieldSection = false;
                        keySection = true;
                    } else if (fieldSection && line.trim().startsWith("@@")) {
                        String[] fieldParts = line.trim().split("\\[");
                        String fieldName = fieldParts[0].replace("@@", "").trim();
                        fields.add(fieldName);
                    } else if(keySection && line.trim().startsWith("@@")) {

                        Pattern keyPattern = Pattern.compile("@@(.*?)\\[(.*?)\\]@@");
                        Matcher keyMatcher = keyPattern.matcher(line);

                        while (keyMatcher.find()) {
                            String keyName = keyMatcher.group(1);
                            String keyType = keyMatcher.group(2);
                            tableKeys.put(keyName, keyType);
                        }
                    }

                }
                keys.put(tableName, tableKeys);


                // Add table and fields to the relationships map
                relationships.put(tableName, fields);
            }
        }

        // Generate the ER diagram
        StringBuilder erDiagram = new StringBuilder();
        for (String table : relationships.keySet()) {
            erDiagram.append("+-----------------------+\t\t\t");
        }
        erDiagram.append("\n");

        for (String table : relationships.keySet()) {
            erDiagram.append("| ").append(String.format("%-20s",table)).append(" |\t\t\t");
        }
        erDiagram.append("\n");

        for (String table : relationships.keySet()) {
            erDiagram.append("+----------------------+\t\t\t");
        }
        erDiagram.append("\n");

        int maxFields = getMaxValue(relationships);
        for (int i=0; i <= maxFields; i++) {
            for (String table : relationships.keySet()) {
                List<String> fields = relationships.get(table);
                Map<String, String> keyData = keys.get(table);
                if (fields.size() > i) {
                    if (keyData.containsKey(fields.get(i)) && keyData.get(fields.get(i)).contains("FOREIGN KEY")) {
                        erDiagram.append("| ").append(String.format("%-20s",fields.get(i))).append(" |\t").append(" ---->  ");
                    } else {
                        erDiagram.append("| ").append(String.format("%-20s",fields.get(i))).append(" |\t\t\t");
                    }
                } else {
                    erDiagram.append("+----------------------+\t\t\t");
                    relationships.remove(table);
                    break;
                }
            }
            erDiagram.append("\n");
        }

        System.out.println(erDiagram.toString());
    }

    public static int getMaxValue (Map<String, List<String>> relationships) {
        int maxValue = Integer.MIN_VALUE;

        for (List<String> values : relationships.values()) {
            int length = values.toArray().length;
            if (length > maxValue) {
                maxValue = length;
            }
        }
        return maxValue;
    }

}
