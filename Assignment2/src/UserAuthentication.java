import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class UserAuthentication {
    private static final String DELIMITER = "::";
    private static final String FILE_PATH = "src/Database/user_credentials.txt";
    private DataStoreService dataStoreService;

    public UserAuthentication(DataStoreService dataStoreService) {
        this.dataStoreService = dataStoreService;
    }

    public boolean registerUser (User user) {
        if(!dataStoreService.validateData(FILE_PATH, user.getUsername(), 1)) {
            String hashedPassword = hashPassword(user.getPassword());
            String userData = user.getUsername() + DELIMITER + hashedPassword + DELIMITER + user.getSecurityQuestion() + DELIMITER + user.getSecurityAnswer();
            dataStoreService.saveDataToFile(FILE_PATH, userData);
            return true;
        }
        return false;
    }

    public boolean authenticateUser (String userName, String password) {
        if(dataStoreService.validateData(FILE_PATH, userName, 1)) {
            String hashedPassword = hashPassword(password);
            String data = dataStoreService.getFieldData(FILE_PATH, userName, 1);
            String[] authData = data.split(DELIMITER);
            if(userName.equals(authData[0]) && hashedPassword.equals(authData[1])) {
                System.out.println("Please answer the below security question - ");
                System.out.println(authData[2]);
                Scanner scanner = new Scanner(System.in);
                String answer = scanner.nextLine().trim();
                return answer.equals(authData[3]);
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
