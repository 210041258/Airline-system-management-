package mysql;

import java.sql.*;

public class CreatearticlesIfNotExists {
    public static void main(String[] args) {
        String jdbcUrl = "jdbc:mysql://localhost:3306/";
        String username = "root";
        String password = "Root@2023";
        String databaseName = "airlines_articles";
        String tableName = "articles";

        String createTableSQL = "CREATE TABLE " + tableName + " (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "content VARCHAR(500) NOT NULL," +
                "title VARCHAR(100) NOT NULL" +
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
