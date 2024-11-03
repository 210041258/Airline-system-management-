package mysql;
import java.sql.*;

public class CreateDatabaseIfNotExists {

    public static void main(String args) {
        String databaseName = "user_databases"; // Default database name

        if (!args.isEmpty()) {  // Check if an argument was provided
            databaseName = args; // Use the provided argument if available
        }


        String jdbcUrl = "jdbc:mysql://localhost:3306/"; // URL without the database name
        String username = "root";  // Replace with your MySQL username
        String password = "Root@2023";  // Replace with your MySQL password


        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery("SHOW DATABASES LIKE '" + databaseName + "'");

            if (!rs.next()) { // Database doesn't exist
                stmt.executeUpdate("CREATE DATABASE " + databaseName);
                System.out.println("Database '" + databaseName + "' created successfully."); // More informative message
            } else {
                System.out.println("Database '" + databaseName + "' already exists."); // More informative message
            }

        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
