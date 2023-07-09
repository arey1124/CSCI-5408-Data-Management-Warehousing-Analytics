import Services.DataBuilderService;
import Services.DataReaderService;
import Services.DataStoreService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UserDatabaseBuilder {

    private DataStoreService dataStoreService;
    private DataBuilderService dataBuilderService;
    private DataReaderService dataReaderService;
    private static final String DATABASE_IDENTIFIER = "@@database@@";
    private static final String FILE_PATH = "src/Database/user_credentials.txt";

    public UserDatabaseBuilder(DataStoreService dataStoreService, DataBuilderService dataBuilderService, DataReaderService dataReaderService) {
        this.dataStoreService = dataStoreService;
        this.dataBuilderService = dataBuilderService;
        this.dataReaderService = dataReaderService;
    }

    public boolean createDatabase (String userName, String dbName) {
        if(userName == null || dbName == null) {
            return false;
        }
        String userData = dataReaderService.getUserData(FILE_PATH, userName);
        String database = dataReaderService.extractValue(userData, DATABASE_IDENTIFIER);
        if(database == null) {
            dataStoreService.appendDatabaseData(FILE_PATH, userName, dataBuilderService.buildDatabaseData(dbName));
            String folderPath = "src/Database/" + userName + "." + dbName;
            Path folder = Paths.get(folderPath);
            try {
                if (!Files.exists(folder)) {
                    Files.createDirectories(folder);
                }
            } catch (Exception e) {
                System.out.println("Failed to create the folder: " + e.getMessage());
                return false;
            }
            return true;
        }
        return false;
    }
}
