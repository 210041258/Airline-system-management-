package hostdevicedata;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Orders {
    private int orderId;
    private String username;
    private int ticketId;
    private int orderCount;
    private Date creationDate;

    // Database connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/user_databases";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Root@2023";
    private static final String ORDERS_TICKET_USERNAME_SQL = "SELECT * FROM orders WHERE username = ? AND ticket_id = ?";

    // Constructor
    public Orders(int orderId, String username, int ticketId, int orderCount, Date creationDate) {
        this.orderId = orderId;
        this.username = username;
        this.ticketId = ticketId;
        this.orderCount = orderCount;
        this.creationDate = creationDate;
        createOrdersTableIfNotExists();
        saveToDatabase();
    }

    // Create table if not exists
    private static void createOrdersTableIfNotExists() {
        String createTableSQL = """
                CREATE TABLE IF NOT EXISTS orders (
                    order_id INT AUTO_INCREMENT PRIMARY KEY,
                    username VARCHAR(255),
                    ticket_id INT,
                    order_count INT,
                    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """;

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Save to database
    public void saveToDatabase() {
        String query = "INSERT INTO orders (username, ticket_id, order_count) VALUES (?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setInt(2, ticketId);
            statement.setInt(3, orderCount);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Load orders by username
    public static List<Orders> loadOrdersByUsername(String username) {
        List<Orders> ordersList = new ArrayList<>();
        String query = "SELECT * FROM orders WHERE username = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Orders order = new Orders(
                            resultSet.getInt("order_id"),
                            resultSet.getString("username"),
                            resultSet.getInt("ticket_id"),
                            resultSet.getInt("order_count"),
                            resultSet.getTimestamp("creation_date")
                    );
                    ordersList.add(order);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ordersList;
    }

    // Update order in database
    public void updateOrderInDatabase() {
        String query = """
            UPDATE orders SET 
                username = COALESCE(?, username),
                ticket_id = COALESCE(?, ticket_id),
                order_count = COALESCE(?, order_count)
            WHERE order_id = ?
            """;

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            statement.setObject(2, ticketId == 0 ? null : ticketId);
            statement.setObject(3, orderCount == 0 ? null : orderCount);
            statement.setInt(4, orderId);

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Order updated successfully.");
            } else {
                System.out.println("No matching order found to update.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Getters and Setters
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
        updateOrderInDatabase();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        updateOrderInDatabase();
    }

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
        updateOrderInDatabase();
    }

    public int getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(int orderCount) {
        this.orderCount = orderCount;
        updateOrderInDatabase();
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public static List<Orders> loadOrdersByUsernameAndTicketId(String username, int ticketId) {
        List<Orders> ordersList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(ORDERS_TICKET_USERNAME_SQL)) {

            statement.setString(1, username);
            statement.setInt(2, ticketId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Orders order = new Orders(
                            resultSet.getInt("order_id"),
                            resultSet.getString("username"),
                            resultSet.getInt("ticket_id"),
                            resultSet.getInt("order_count"),
                            resultSet.getTimestamp("creation_date")
                    );
                    ordersList.add(order);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ordersList;
    }
}
