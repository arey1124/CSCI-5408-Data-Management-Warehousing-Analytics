package Services;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DataStoreService {
    private static final String DELIMITER = "@@";

    public void saveDataToFile(String filePath, String data) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, true))) {
            writer.println(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void appendDatabaseData(String filePath, String identifier, String dataToAppend) {
        File originalFile = new File(filePath);
        File tempFile = new File("temp.txt");
        try (BufferedReader reader = new BufferedReader(new FileReader(originalFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            StringBuilder fileContent = new StringBuilder();
            String line;
            boolean foundItem = false, modified = false;

            while ((line = reader.readLine()) != null) {
                String indentation = getIndentation(line);
                if (line.trim().equals("@@" + identifier + "@@")) {
                    if(foundItem) {

                        fileContent.append(dataToAppend);
                        fileContent.append(indentation + line.trim()).append(System.lineSeparator());
                        foundItem = false;
                        modified = true;
                        continue;
                    } else{
                        foundItem = true;
                    }
                }
                fileContent.append(indentation + line.trim()).append(System.lineSeparator());
            }
            if(modified){
                writer.write(fileContent.toString());
                originalFile.delete();
                tempFile.renameTo(new File(filePath));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getIndentation(String line) {
        StringBuilder indentation = new StringBuilder();
        int index = 0;

        while (index < line.length() && Character.isWhitespace(line.charAt(index))) {
            indentation.append(line.charAt(index));
            index++;
        }

        return indentation.toString();
    }

    public void addTableHeadersTofile(File file, Map<String,String> fields) throws IOException {
        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write(String.join("@@", fields.keySet()));
        bufferedWriter.close();
    }

    public void addDataToTable(File file, List<String> data) throws IOException {
        FileWriter fileWriter = new FileWriter(file, true);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write("\n" + String.join("@@", data));
        bufferedWriter.close();
    }

    public void updateTableData(File file, String colToUpdate,
                                String updatedValue, String conditionCol, String conditionValue) {
        try {
            List<String> columns;
            // Read the data from the file
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String header = reader.readLine(); // Skip the header line
            columns = Arrays.asList(header.split("@@"));

            String line;
            StringBuilder updatedData = new StringBuilder();
            boolean recordUpdated = false;

            // Process each line in the file
            while ((line = reader.readLine()) != null) {
                String[] values = line.split("@@");
                String valOfConditionalCol = values[columns.indexOf(conditionCol)];
                if (valOfConditionalCol.equals(conditionValue)) {
                    // Update the customer ID
                    values[columns.indexOf(colToUpdate)] = updatedValue;
                    recordUpdated = true;
                }

                // Append the updated line to the StringBuilder
                updatedData.append(String.join("@@", values)).append("\n");
            }

            reader.close();

            if (recordUpdated) {
                // Write the updated data back to the file
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                writer.write(header + "\n"); // Write the header line
                writer.write(updatedData.toString()); // Write the updated data lines
                writer.close();

                System.out.println("Update successful.");
            } else {
                System.out.println("No record found with the specified condition.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeTableData (File file, String columnName, String columnValue){
        try {
            List<String> columns;
            // Read the data from the file
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String header = reader.readLine(); // Skip the header line
            columns = Arrays.asList(header.split("@@"));

            String line;
            StringBuilder updatedData = new StringBuilder();
            boolean recordDeleted = false;

            // Process each line in the file
            while ((line = reader.readLine()) != null) {
                String[] values = line.split("@@");
                String valOfConditionalCol = values[columns.indexOf(columnName)];
                if (valOfConditionalCol.equals(columnValue)) {
                    recordDeleted = true;
                } else {
                    // Append the updated line to the StringBuilder
                    updatedData.append(String.join("@@", values)).append("\n");
                }
            }
            reader.close();

            if (recordDeleted) {
                // Write the updated data back to the file
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                writer.write(header + "\n"); // Write the header line
                writer.write(updatedData.toString()); // Write the updated data lines
                writer.close();

                System.out.println("Delete successful.");
            } else {
                System.out.println("No record found with the specified condition.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
