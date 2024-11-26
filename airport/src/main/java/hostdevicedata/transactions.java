package hostdevicedata;

import java.sql.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Transaction {
    private int transactionId;
    private String username;
    private double amount;
    private Date date;
    private String typeTransaction; // New attribute: type of the transaction
    private String transactionMessage; // New attribute: message associated with the transaction

    // Database connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/user_databases";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Root@2023";
    private static final String FILE_PATH = "transaction_index.txt"; // Path to the transaction file
    private static final String select_transactions_by_amount_domain_sql = "SELECT * FROM transactions WHERE amount = ?";
    private static final String any_exsiting_username_sql = "SELECT * FROM transactions WHERE username = ? ";//_before_searching_
    private static final String select_transactions_by_type_transaction_sql = "SELECT * FROM transactions WHERE type_transaction = ?";
    private static final String select_transactions_by_days = "SELECT * FROM transactions WHERE date BETWEEN ? AND ?"; //dates_ago_calculatednumber_of_days_before_sql
    private static final String select_id_by_days = "SELECT * FROM transactions WHERE transaction_id = ?"; //dates_ago_calculatednumber_of_days_before_sql

    public static List<Transaction> getTransactionsByDateRange(Date startDate, Date endDate) {
        List<Transaction> transactions = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(select_transactions_by_days)) {
            
            statement.setDate(1, startDate);
            statement.setDate(2, endDate);
            
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                Transaction transaction = new Transaction(
                        resultSet.getInt("transaction_id"),
                        resultSet.getString("username"),
                        resultSet.getDouble("amount"),
                        resultSet.getString("type_transaction"),
                        resultSet.getDate("date")
                );
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }
    

    public static Transaction getTransactionById(int transactionId) {
        Transaction transaction = null;
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(select_id_by_days)) {
            statement.setInt(1, transactionId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                transaction = new Transaction(
                        resultSet.getInt("transaction_id"),
                        resultSet.getString("username"),
                        resultSet.getDouble("amount"),
                        resultSet.getString("type_transaction"),
                        resultSet.getDate("date")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transaction;
    }


    public static List<Transaction> getTransactionsByType(String typeTransaction) {
        List<Transaction> transactions = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(select_transactions_by_type_transaction_sql)) {
            statement.setString(1, typeTransaction);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Transaction transaction = new Transaction(
                        resultSet.getInt("transaction_id"),
                        resultSet.getString("username"),
                        resultSet.getDouble("amount"),
                        resultSet.getString("type_transaction"),
                        resultSet.getDate("date")
                );
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }





    public static List<Transaction> getTransactionsByUsername(String username) {
        List<Transaction> transactions = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(any_exsiting_username_sql)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Transaction transaction = new Transaction(
                        resultSet.getInt("transaction_id"),
                        resultSet.getString("username"),
                        resultSet.getDouble("amount"),
                        resultSet.getString("type_transaction"),
                        resultSet.getDate("date")
                );
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    public static List<Transaction> getTransactionsByAmount(double amount) {
        List<Transaction> transactions = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(select_transactions_by_amount_domain_sql)) {
            statement.setDouble(1, amount);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Transaction transaction = new Transaction(
                        resultSet.getInt("transaction_id"),
                        resultSet.getString("username"),
                        resultSet.getDouble("amount"),
                        resultSet.getString("type_transaction"),
                        resultSet.getDate("date")
                );
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }
    
    // Constructor
    public Transaction(int transactionId, String username, double amount, Date date, String typeTransaction, String transactionMessage) {
        this.transactionId = transactionId;
        this.username = username;
        this.amount = amount;
        this.date = date;
        this.typeTransaction = typeTransaction;
        this.transactionMessage = transactionMessage;
    }

    // Method to create the database and table
    public static void createDatabaseAndTable() {
        String createDatabaseSQL = "CREATE DATABASE IF NOT EXISTS user_databases";
        String createTableSQL = "CREATE TABLE IF NOT EXISTS transactions (" +
                "transaction_id INT AUTO_INCREMENT PRIMARY KEY," +
                "username VARCHAR(50) NOT NULL," +
                "amount DECIMAL(10, 2) NOT NULL," +
                "date DATE NOT NULL," +
                "type_transaction VARCHAR(50) NOT NULL," + // New column for type-transaction
                "transaction_message TEXT" + // New column for transaction-message
                ")";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(createDatabaseSQL);
            statement.executeUpdate(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to save transaction to the database
    public void saveToDatabase() {
        String query = "INSERT INTO transactions (username, amount, date, type_transaction, transaction_message) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setDouble(2, amount);
            statement.setDate(3, date);
            statement.setString(4, typeTransaction);
            statement.setString(5, transactionMessage);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to load transactions from a text file
    public static void loadTransactionsFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(","); // Assuming CSV format
                if (parts.length == 6) {
                    Transaction transaction = new Transaction(
                            Integer.parseInt(parts[0]), // transaction_id
                            parts[1], // username
                            Double.parseDouble(parts[2]), // amount
                            Date.valueOf(parts[3]), // date
                            parts[4], // type_transaction
                            parts[5] // transaction_message
                    );
                    transaction.saveToDatabase(); // Save each transaction to the database
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to create and populate the transaction_index.txt file
    public static void createTransactionIndexFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            // Write header
            writer.println("transaction_id,username,amount,date,type_transaction,transaction_message");
            System.out.println("Transaction index file created successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to retrieve transactions for a specific user
    public static List<Transaction> getTransactionsForUser(String username) {
        List<Transaction> transactions = new ArrayList<>();
        String query = "SELECT * FROM transactions WHERE username = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Transaction transaction = new Transaction(
                        resultSet.getInt("transaction_id"),
                        resultSet.getString("username"),
                        resultSet.getDouble("amount"),
                        resultSet.getDate("date"),
                        resultSet.getString("type_transaction"),
                        resultSet.getString("transaction_message")
                );
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    private void updateTransactionFile() {
        List<Transaction> transactions = loadAllTransactionsFromFile();

        // Find and update the matching transaction
        for (Transaction transaction : transactions) {
            if (transaction.getTransactionId() == this.transactionId) {
                transaction.setUsername(this.username);
                transaction.setAmount(this.amount);
                transaction.setDate(this.date);
                transaction.setTypeTransaction(this.typeTransaction);
                transaction.setTransactionMessage(this.transactionMessage);
                break;
            }
        }

        // Rewrite the file with updated transactions
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            writer.println("transaction_id,username,amount,date,type_transaction,transaction_message");
            for (Transaction transaction : transactions) {
                writer.printf("%d,%s,%.2f,%s,%s,%s%n",
                        transaction.getTransactionId(),
                        transaction.getUsername(),
                        transaction.getAmount(),
                        transaction.getDate(),
                        transaction.getTypeTransaction(),
                        transaction.getTransactionMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to load all transactions from the text file
    private static List<Transaction> loadAllTransactionsFromFile() {
        List<Transaction> transactions = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line = reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 6) {
                    Transaction transaction = new Transaction(
                            Integer.parseInt(parts[0]), // transaction_id
                            parts[1], // username
                            Double.parseDouble(parts[2]), // amount
                            Date.valueOf(parts[3]), // date
                            parts[4], // type_transaction
                            parts[5] // transaction_message
                    );
                    transactions.add(transaction);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
        updateTransactionFile();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        updateTransactionFile();
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
        updateTransactionFile();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
        updateTransactionFile();
    }

    public String getTypeTransaction() {
        return typeTransaction;
    }

    public void setTypeTransaction(String typeTransaction) {
        this.typeTransaction = typeTransaction;
        updateTransactionFile();
    }

    public String getTransactionMessage() {
        return transactionMessage;
    }

    public void setTransactionMessage(String transactionMessage) {
        this.transactionMessage = transactionMessage;
        updateTransactionFile();
    }






}
