package mysql;

import java.sql.*;

public class CreateusersIfNotExists {

    public static void main() {
        String jdbcUrl = "jdbc:mysql://localhost:3306/";
        String username = "root";
        String password = "Root@2023";
        String databaseName = "user_databases";
        String tableName = "users";

        String createTableSQL = "CREATE TABLE " + tableName + " (" +
                "username VARCHAR(50) NOT NULL UNIQUE PRIMARY KEY," +
                "email VARCHAR(50) NOT NULL UNIQUE," +
                "password VARCHAR(50) NOT NULL," +
                "balance DECIMAL(10, 0) NOT NULL DEFAULT 100000"+
                ")";

        String dropTableSQL = "DROP TABLE IF EXISTS " + tableName;

        try (Connection serverConn = DriverManager.getConnection(jdbcUrl, username, password)) {

            try (Statement dbStmt = serverConn.createStatement()) {
                ResultSet dbExistsRs = dbStmt.executeQuery("SHOW DATABASES LIKE '" + databaseName + "'");
                if (!dbExistsRs.next()) {
                    dbStmt.executeUpdate("CREATE DATABASE " + databaseName);
                    System.out.println("Database '" + databaseName + "' created.");
                }
            }

            String dbUrl = "jdbc:mysql://localhost:3306/" + databaseName;
            try (Connection dbConn = DriverManager.getConnection(dbUrl, username, password);
                 Statement tableStmt = dbConn.createStatement()) {

                DatabaseMetaData metaData = dbConn.getMetaData();
                ResultSet rs = metaData.getTables(null, null, tableName, null);
                if (!rs.next()) {
                    tableStmt.executeUpdate(createTableSQL);
                } else {
                    tableStmt.executeUpdate(dropTableSQL);
                    tableStmt.executeUpdate(createTableSQL);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}