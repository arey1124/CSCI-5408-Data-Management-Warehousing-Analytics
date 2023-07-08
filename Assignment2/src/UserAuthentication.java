import Model.User;
import Services.DataBuilderService;
import Services.DataReaderService;
import Services.DataStoreService;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class UserAuthentication {
    private static final String PASSWORD_IDENTIFIER = "@@password@@";
    private static final String SECURITY_QUESTION_IDENTIFIER = "@@question@@";
    private static final String SECURITY_ANSWER_IDENTIFIER = "@@answer@@";
    private static final String FILE_PATH = "src/Database/user_credentials.txt";
    private DataStoreService dataStoreService;
    private DataBuilderService dataBuilderService;
    private DataReaderService dataReaderService;

    public UserAuthentication(DataStoreService dataStoreService, DataBuilderService dataBuilderService, DataReaderService dataReaderService) {
        this.dataStoreService = dataStoreService;
        this.dataBuilderService = dataBuilderService;
        this.dataReaderService = dataReaderService;
    }

    public boolean registerUser (User user) {
        if(dataReaderService.getUserData(FILE_PATH, user.getUsername()) == null) {
            String hashedPassword = hashPassword(user.getPassword());
            dataStoreService.saveDataToFile(FILE_PATH, dataBuilderService.buildUserData(user.getUsername(), hashedPassword, user.getSecurityQuestion(), user.getSecurityAnswer()));
            return true;
        }
        return false;
    }

    public boolean authenticateUser (String userName, String password) {
        String userData = dataReaderService.getUserData(FILE_PATH, userName);
        if(!userData.isEmpty() && userData != null) {
            String hashedPassword = hashPassword(password);
            String userPassword = dataReaderService.extractValue(userData, PASSWORD_IDENTIFIER);
            if(hashedPassword.equals(userPassword)) {
                System.out.println("Please answer the below security question - ");
                System.out.println(dataReaderService.extractValue(userData, SECURITY_QUESTION_IDENTIFIER));
                Scanner scanner = new Scanner(System.in);
                String answer = scanner.nextLine().trim();
                return answer.equals(dataReaderService.extractValue(userData, SECURITY_ANSWER_IDENTIFIER));
            }
        }
        return false;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] passwordBytes = password.getBytes();
            byte[] hashBytes = messageDigest.digest(passwordBytes);

            StringBuilder stringBuilder = new StringBuilder();
            for (byte hashByte : hashBytes) {
                stringBuilder.append(Integer.toString((hashByte & 0xff) + 0x100, 16).substring(1));
            }
            return stringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
