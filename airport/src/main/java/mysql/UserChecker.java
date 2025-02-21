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


}