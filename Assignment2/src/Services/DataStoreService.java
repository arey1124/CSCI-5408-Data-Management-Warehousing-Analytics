package Services;

import java.io.*;

public class DataStoreService {
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
}
