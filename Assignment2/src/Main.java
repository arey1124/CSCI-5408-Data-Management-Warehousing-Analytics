import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        boolean isUserLoggedIn = false;

        Scanner scanner = new Scanner(System.in);

        String username, password, question, answer, loggedInUser;
        User user;
        UserAuthentication auth = new UserAuthentication(new DataStoreService());

        while (true && !isUserLoggedIn) {
            System.out.println("Select the operation:");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            String option = scanner.nextLine();

            switch (option) {
                case "1":
                    System.out.println("Enter user name");
                    username = scanner.nextLine();
                    System.out.println("Enter password");
                    password = scanner.nextLine();
                    isUserLoggedIn = auth.authenticateUser(username, password);
                    if(isUserLoggedIn) {
                        loggedInUser = username;
                        System.out.println("Successfully logged in");
                    } else {
                        System.out.println("Invalid credentials");
                    }
                    break;
                case "2":
                    System.out.println("Enter user name");
                     username = scanner.nextLine();
                    System.out.println("Enter password");
                    password = scanner.nextLine();
                    System.out.println("Enter security question");
                    question = scanner.nextLine();
                    System.out.println("Enter security answer");
                    answer = scanner.nextLine();
                    user = new User(username, password, question, answer);
                    if(auth.registerUser(user)) {
                        System.out.println("User registered successfully.");
                    } else {
                        System.out.println("User with username already exists");
                    }
                    break;
                case "3":
                    System.out.println("Exiting the program...");
                    break;
                default:
                    System.out.println("Select a valid option");
                    break;
            }
        }

        while (isUserLoggedIn) {
            System.out.println("Select the operation:");
            System.out.println("1. Create a Database");
            System.out.println("2. Execute Query");
            System.out.println("3. Logout & Exit");
            String option = scanner.nextLine();
            switch (option) {
                case "1":
                    break;
                default:
                    System.out.println("Select a valid option");
                    break;
            }
        }
    }
}