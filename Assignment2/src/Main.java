import Model.User;
import Services.*;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        boolean isUserLoggedIn = false;

        Scanner scanner = new Scanner(System.in);

        String username, password, question, answer, loggedInUser = null;
        User user;
        UserAuthentication auth = new UserAuthentication(new DataStoreService(), new DataBuilderService(), new DataReaderService());
        UserDatabaseBuilder databaseBuilder = new UserDatabaseBuilder(new DataStoreService(), new DataBuilderService(), new DataReaderService());

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
                        System.out.println("Model.User registered successfully.");
                    } else {
                        System.out.println("Model.User with username already exists");
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
            System.out.println("3. Generate ER Diagram");
            System.out.println("4. Logout & Exit");
            String option = scanner.nextLine();
            switch (option) {
                case "1":
                    System.out.println("Enter the database name to create");
                    String dbName = scanner.nextLine();
                    boolean isCreated = databaseBuilder.createDatabase(loggedInUser, dbName);
                    if(isCreated) {
                        System.out.println("Successfully created database");
                    } else {
                        System.out.println("Each user is limited to one database only");
                    }
                    break;
                case "2":
                    System.out.println("Enter the query to execute");
                    StringBuilder queryBuilder = new StringBuilder();
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        if (line.isEmpty() || line.isBlank()) {
                            break; // Exit the loop if an empty line is encountered
                        }
                        queryBuilder.append(line).append(" ");
                    }
                    String query = queryBuilder.toString().trim();
                    QueryService queryService = new QueryService(new DataStoreService(), new DataBuilderService(), new DataReaderService());
                    boolean isQueryExecuted = queryService.execute(loggedInUser, query);
                    break;
                case "3":
                    ERDiagramGenerator erDiagramGenerator = new ERDiagramGenerator();
                    erDiagramGenerator.generateERDiagram(new DataReaderService().getTableDesc(loggedInUser));
                    break;
                default:
                    System.out.println("Select a valid option");
                    break;
            }
        }
    }
}