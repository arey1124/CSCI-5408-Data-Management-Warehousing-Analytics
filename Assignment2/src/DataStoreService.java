import java.io.*;

public class DataStoreService {
    private static final String DELIMITER = "::";
    public void saveDataToFile(String filePath, String data) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, true))) {
            writer.println(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean validateData(String filePath, String data, int position) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fileData = line.split(DELIMITER);
                if(fileData[position-1].equals(data)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getFieldData (String filePath, String identifier, int position) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fileData = line.split(DELIMITER);
                if(fileData[position-1].equals(identifier)) {
                    return line;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
