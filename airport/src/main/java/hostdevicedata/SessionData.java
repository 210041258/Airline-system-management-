package hostdevicedata;

import java.sql.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SessionData {

    private static String username;
    private static String email;
    private static double balance;
    private static List<String> transactions = new ArrayList<>();
    private static List<String> tickets = new ArrayList<>();
    private static final String SESSION_FILE = "session_data.txt";

    private static final String DB_URL = "jdbc:mysql://localhost:3306/user_databases";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Root@2023";

    public SessionData(String username, String email, double balance) {
        this.username = username;
        this.email = email;
        this.balance = balance;
        this.transactions = new ArrayList<>();
        this.tickets = new ArrayList<>();
    }


    // Getters and Setters
    public static void setUsername(String username) {
        SessionData.username = username;
    }

    public static String getUsername() {
        return username;
    }

    public static void setEmail(String email) {
        SessionData.email = email;
    }

    public static String getEmail() {
        return email;
    }

    public static void setBalance(double balance) {
        SessionData.balance = balance;
    }

    public static double getBalance() {
        return balance;
    }

    public static void setTransactions(List<String> transactions) {
        SessionData.transactions = transactions;
    }

    public static List<String> getTransactions() {
        return transactions;
    }

    public static void setTickets(List<String> tickets) {
        SessionData.tickets = tickets;
    }

    public static List<String> getTickets() {
        return tickets;
    }

    // Clear session data and delete file
    public static void clear() {
        username = null;
        balance = 0;
        email = null;
        transactions.clear();
        tickets.clear();
        new File(SESSION_FILE).delete();
    }

    // Save session to file
    public static void saveSessionToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(SESSION_FILE))) {
            writer.println(username);
            writer.println(email);
            writer.println(balance);
            for (String transaction : transactions) {
                writer.println(transaction);
            }
            for (String ticket : tickets) {
                writer.println(ticket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load session from file
    public static void loadSessionFromFile() {
        File sessionFile = new File(SESSION_FILE);
        if (!sessionFile.exists()) {
            System.out.println("Session file does not exist.");
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(SESSION_FILE))) {
            username = reader.readLine();
            email = reader.readLine();
            balance = Double.parseDouble(reader.readLine());
            String line;

            transactions.clear();
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                transactions.add(line);
            }

            tickets.clear();
            while ((line = reader.readLine()) != null) {
                tickets.add(line);
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    // Database Interaction
    public static List<String> getTransactionsForUser(String username) {
        List<String> userTransactions = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT transaction FROM transactions WHERE username = ?");
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                userTransactions.add(resultSet.getString("transaction"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userTransactions;
    }

    public static List<String> getTicketsForUser(String username) {
        List<String> userTickets = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT ticket FROM tickets WHERE username = ?");
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                userTickets.add(resultSet.getString("ticket"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userTickets;
    }

    // Save session to database
    public static void saveSessionToDatabase() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO session_data (username, email, balance) VALUES (?, ?, ?) " +
                            "ON DUPLICATE KEY UPDATE email = ?, balance = ?");
            statement.setString(1, username);
            statement.setString(2, email);
            statement.setDouble(3, balance);
            statement.setString(4, email);
            statement.setDouble(5, balance);
            statement.executeUpdate();

            // Save transactions with batch processing
            statement = connection.prepareStatement("INSERT INTO transactions (username, transaction) VALUES (?, ?)");
            for (String transaction : transactions) {
                statement.setString(1, username);
                statement.setString(2, transaction);
                statement.addBatch();
            }
            statement.executeBatch();

            // Save tickets with batch processing
            statement = connection.prepareStatement("INSERT INTO tickets (username, ticket) VALUES (?, ?)");
            for (String ticket : tickets) {
                statement.setString(1, username);
                statement.setString(2, ticket);
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Create tables if they don't exist
    public static void createTablesIfNotExist() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            Statement statement = connection.createStatement();

            // Create session_data table
            String createSessionDataTable = "CREATE TABLE IF NOT EXISTS session_data (" +
                    "username VARCHAR(255) PRIMARY KEY, " +
                    "email VARCHAR(255), " +
                    "balance DOUBLE)";
            statement.executeUpdate(createSessionDataTable);

            // Create transactions table
            String createTransactionsTable = "CREATE TABLE IF NOT EXISTS transactions (" +
                    "transaction_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(255), " +
                    "transaction TEXT, " +
                    "FOREIGN KEY (username) REFERENCES session_data(username))";
            statement.executeUpdate(createTransactionsTable);

            // Create tickets table
            String createTicketsTable = "CREATE TABLE IF NOT EXISTS tickets (" +
                    "ticket_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(255), " +
                    "ticket TEXT, " +
                    "FOREIGN KEY (username) REFERENCES session_data(username))";
            statement.executeUpdate(createTicketsTable);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Load session data for a specific user from the database
    public static void loadSessionForUser(String username) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Load user session data (username, email, balance)
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT username, email, balance FROM session_data WHERE username = ?");
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                SessionData.setUsername(resultSet.getString("username"));
                SessionData.setEmail(resultSet.getString("email"));
                SessionData.setBalance(resultSet.getDouble("balance"));
            } else {
                System.out.println("No session found for username: " + username);
                return;
            }

            // Load user transactions
            List<String> userTransactions = new ArrayList<>();
            statement = connection.prepareStatement(
                    "SELECT transaction FROM transactions WHERE username = ?");
            statement.setString(1, username);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                userTransactions.add(resultSet.getString("transaction"));
            }
            SessionData.setTransactions(userTransactions);

            // Load user tickets
            List<String> userTickets = new ArrayList<>();
            statement = connection.prepareStatement(
                    "SELECT ticket FROM tickets WHERE username = ?");
            statement.setString(1, username);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                userTickets.add(resultSet.getString("ticket"));
            }
            SessionData.setTickets(userTickets);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
