import java.sql.*;

public class Main {
    public static void main(String[] args) {
        processOrderAndCheckInventory();
    }

    private static void processOrderAndCheckInventory () {
        String jdbcUrl = "jdbc:mysql://34.133.219.255:3306/lab4?useSSL=false";
        String username = "root";
        String password = "yc:f/Gvh=`UI~l7c";
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            System.out.println("Connected to the Google cloud remote database successfully!");

            // Generates a random value between [1-5] as we have 5 items in our remote database.
            int item_id = 1 + (int)(Math.random() * ((5 - 1) + 1));

            String query = "SELECT * FROM Inventory WHERE item_id=" + item_id;

            //Fetch Item details to verify the available quantity before adding it to order_info table
            Statement statement = connection.createStatement();

            // Get the current time before executing the query
            long startTime = System.currentTimeMillis();

            ResultSet resultSet = statement.executeQuery(query);

            // Get the current time after executing the query
            long endTime = System.currentTimeMillis();
            // Calculate the execution time in milliseconds
            long executionTime = endTime - startTime;
            System.out.println("Query execution time for fetching Inventory from Google Cloud: " + executionTime + " ms");

            // Process the result set
            while (resultSet.next()) {
                int itemId = resultSet.getInt("item_id");
                String itemName = resultSet.getString("item_name");
                int availableQuantity = resultSet.getInt("available_quantity");

                if(availableQuantity > 0) {
                    placeOrder(itemId, itemName);

                    // Update the inventory in remote database
                    //Since we are placing item for 1 quantity, the updated quantity will reduce by 1
                    query = "UPDATE Inventory SET available_quantity = "+ (availableQuantity - 1) +" WHERE item_id = " + itemId;
                    statement = connection.createStatement();

                    // Get the current time before executing the query
                    startTime = System.currentTimeMillis();

                    statement.executeUpdate(query);

                    // Get the current time after executing the query
                    endTime = System.currentTimeMillis();
                    // Calculate the execution time in milliseconds
                    executionTime = endTime - startTime;
                    System.out.println("Query execution time for updating Inventory record in Google Cloud: " + executionTime + " ms");
                } else {
                    System.out.println("Unable to place order since the product is out of stock!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void placeOrder(int itemId, String itemName) {
        String jdbcUrl = "jdbc:mysql://localhost:3306/lab4?useSSL=false";
        String username = "root";
        String password = "password";
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            System.out.println("Connected to the local database successfully!");

            // Using user id as 1 aas default for placing order
            // Using default quantity as 1 for the order
            String query = "INSERT INTO Order_info (user_id, item_name, quantity, order_date) VALUES(1, '"+itemName+"',1, CURDATE())";
            Statement statement = connection.createStatement();

            // Get the current time before executing the query
            long startTime = System.currentTimeMillis();

            statement.executeUpdate(query);

            // Get the current time after executing the query
            long endTime = System.currentTimeMillis();
            // Calculate the execution time in milliseconds
            long executionTime = endTime - startTime;
            System.out.println("Query execution time for updating Order info in Local DB: " + executionTime + " ms");


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}