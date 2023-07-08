package Services;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class DataReaderService {
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

    public String extractValue(String data, String identifier) {
        int startIndex = data.indexOf(identifier) + identifier.length();
        int endIndex = data.indexOf(identifier, startIndex);

        if (startIndex != -1 && endIndex != -1) {
            return data.substring(startIndex, endIndex);
        } else {
            return null;
        }
    }
 }
