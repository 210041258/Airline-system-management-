package mysql;

import java.sql.*;

public class UserChecker {
    private final String jdbcUrl;
    private final String dbUsername;
    private final String dbPassword;

    public UserChecker(String jdbcUrl, String dbUsername, String dbPassword) {
        this.jdbcUrl = jdbcUrl;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
    }


    public boolean checkExistingUser(String username, String email) throws SQLException {
        if (username == null || username.isEmpty() || email == null || email.isEmpty()) {
            return false;
        }

        try (Connection conn = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);
             PreparedStatement pstmt = conn.prepareStatement("SELECT 1 FROM users WHERE username = ? OR email = ?")) {
            pstmt.setString(1, username);
            pstmt.setString(2, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                return !rs.next();
            }
        }
    }


    public static void main(String[] args) {  // Changed return type to void
        String jdbcUrl = "jdbc:mysql://localhost:3306/user_databases";
        String dbUsername = "root";
        String dbPassword = "Root@2023";


        if (args.length != 2) {  // Check for correct number of arguments first
            System.err.println("Usage: java UserChecker <username> <email>");
            System.exit(1); // Indicate an error
        }


        UserChecker checker = new UserChecker(jdbcUrl, dbUsername, dbPassword);

        try {
            if (checker.checkExistingUser(args[0], args[1])) {
                System.out.println("Username and email are available.");
                System.exit(0); // Indicate success (optional)

            } else {
                System.err.println("Username or email already exists.");
                System.exit(1); // Indicate an error/user exists (optional)

            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1); // Exit with error code if there's a database problem
        }
    }
}