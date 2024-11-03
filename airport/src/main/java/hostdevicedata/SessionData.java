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



    public static void clear() {
        username = null;
        balance = 0;
        email = null;
        transactions.clear();
        tickets.clear();
        new File(SESSION_FILE).delete();
    }

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

    public static void loadSessionFromFile() {
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

            statement = connection.prepareStatement("INSERT INTO transactions (username, transaction) VALUES (?, ?)");
            for (String transaction : transactions) {
                statement.setString(1, username);
                statement.setString(2, transaction);
                statement.executeUpdate();
            }

            statement = connection.prepareStatement("INSERT INTO tickets (username, ticket) VALUES (?, ?)");
            for (String ticket : tickets) {
                statement.setString(1, username);
                statement.setString(2, ticket);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
