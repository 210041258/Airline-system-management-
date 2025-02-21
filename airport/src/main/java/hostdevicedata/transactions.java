package hostdevicedata;

import java.sql.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class transactions {
    private int transactionId;
    private String username;
    private double amount;
    private Date date;
    private String typeTransaction;
    private String transactionMessage;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/user_databases";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Root@2023";
    private static final String FILE_PATH = "transaction_index.txt"; // Path to the transaction file

    private static final String SELECT_TRANSACTIONS_BY_AMOUNT_SQL = "SELECT * FROM transactions WHERE amount = ? BETWEEN ? ";
    private static final String SELECT_TRANSACTIONS_BY_USERNAME_SQL = "SELECT * FROM transactions WHERE username = ?";
    private static final String SELECT_TRANSACTIONS_BY_TYPE_SQL = "SELECT * FROM transactions WHERE type_transaction = ?";
    private static final String SELECT_TRANSACTIONS_BY_DATE_RANGE_SQL = "SELECT * FROM transactions WHERE date BETWEEN ? AND ?";
    private static final String SELECT_TRANSACTION_BY_ID_SQL = "SELECT * FROM transactions WHERE transaction_id = ?";

    public transactions(int transactionId, String username, double amount, Date date, String typeTransaction, String transactionMessage) {
        this.transactionId = transactionId;
        this.username = username;
        this.amount = amount;
        this.date = date;
        this.typeTransaction = typeTransaction;
        this.transactionMessage = transactionMessage;
        createTransactionsTableIfNotExists();
    }

    public transactions() {
    }

    private static void createTransactionsTableIfNotExists() {


        String createTableSQL = "CREATE TABLE IF NOT EXISTS transactions (" +
                "transaction_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "username VARCHAR(50) NOT NULL, " +
                "amount DECIMAL(10, 2) NOT NULL, " +
                "date DATE NOT NULL, " +
                "type_transaction VARCHAR(50) NOT NULL, " +
                "transaction_message TEXT," +
                "CONSTRAINT fk_username_tr FOREIGN KEY (username) REFERENCES users(username)"+
                ")";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public static List<transactions> loadAllTransactionsFromDatabase()  {
        List<transactions> transactionsList = new ArrayList<>();
        String query = "SELECT * FROM transactions WHERE username = ?";
        String username = getUsernameFromSessionFile();
        if (username == null || username.isEmpty()) {
            System.out.println("No valid session found. Username could not be extracted.");
            return null;
        }

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    transactions transaction = new transactions(
                            resultSet.getInt("transaction_id"),
                            resultSet.getString("username"),
                            resultSet.getDouble("amount"),
                            resultSet.getDate("date"),
                            resultSet.getString("type_transaction"),
                            resultSet.getString("transaction_message")
                    );
                    transactionsList.add(transaction); // Add transaction to the list
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transactionsList;
    }


    public static String getUsernameFromSessionFile() {
        File directory = new File(".");
        File[] files = directory.listFiles((dir, name) -> name.endsWith("_session.txt")); // Filter for session files

        if (files != null && files.length > 0) {
            File sessionFile = files[0];

            try (BufferedReader reader = new BufferedReader(new FileReader(sessionFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("Username:")) {
                        return line.substring("Username:".length()).trim(); // Extract and return username
                    }
                }
            } catch (IOException e) {
                System.err.println("Error reading session file: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return null;
    }

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
            updateFile();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to update the transaction file after each operation
    private void updateFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            // Write the transaction details to the file
            writer.append(transactionId + "," + username + "," + amount + "," + date + "," + typeTransaction + "," + transactionMessage);
            writer.newLine(); // Add a new line after each transaction
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to update an existing transaction in the file and the database
    public void updateTransactionInDatabase() {
        String query = "UPDATE transactions SET username = ?, amount = ?, date = ?, type_transaction = ?, transaction_message = ? WHERE transaction_id = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setDouble(2, amount);
            statement.setDate(3, date);
            statement.setString(4, typeTransaction);
            statement.setString(5, transactionMessage);
            statement.setInt(6, transactionId);
            statement.executeUpdate();

            // Update the file after modifying the database entry
            updateFile();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to load all transactions from the file
    public static List<transactions> loadAllTransactionsFromFile() {
        List<transactions> transactionsList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line = reader.readLine(); // Skip header if necessary
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 6) {
                    transactions transaction = new transactions(
                            Integer.parseInt(parts[0]), // transaction_id
                            parts[1], // username
                            Double.parseDouble(parts[2]), // amount
                            Date.valueOf(parts[3]), // date
                            parts[4], // type_transaction
                            parts[5] // transaction_message
                    );
                    transactionsList.add(transaction);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return transactionsList;
    }

    // Getter and setter methods for the class properties
    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTypeTransaction() {
        return typeTransaction;
    }

    public void setTypeTransaction(String typeTransaction) {
        this.typeTransaction = typeTransaction;
    }

    public String getTransactionMessage() {
        return transactionMessage;
    }

    public void setTransactionMessage(String transactionMessage) {
        this.transactionMessage = transactionMessage;
    }


    public List<transactions> selectTransactionsByAmount(double amount) throws SQLException {
        String sql = SELECT_TRANSACTIONS_BY_AMOUNT_SQL;
        List<transactions> transactions = new ArrayList<>();        Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setDouble(1, amount);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                transactions.add(mapRowToTransaction(resultSet));
            }
        }
        return transactions;
    }

    // Function to select transactions by username
    public List<transactions> selectTransactionsByUsername(String username) throws SQLException {
        String sql = SELECT_TRANSACTIONS_BY_USERNAME_SQL;
        List<transactions> transactions = new ArrayList<>();        Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                transactions.add(mapRowToTransaction(resultSet));
            }
        }
        return transactions;
    }

    // Function to select transactions by type
    public List<transactions> selectTransactionsByType(String typeTransaction) throws SQLException {
        String sql = SELECT_TRANSACTIONS_BY_TYPE_SQL;
        List<transactions> transactions = new ArrayList<>();        Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, typeTransaction);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                transactions.add(mapRowToTransaction(resultSet));
            }
        }
        return transactions;
    }

    // Function to select transactions by date range
    public List<transactions> selectTransactionsByDateRange(String startDate, String endDate) throws SQLException {
        String sql = SELECT_TRANSACTIONS_BY_DATE_RANGE_SQL;        Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

        List<transactions> transactions = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, startDate);
            preparedStatement.setString(2, endDate);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                transactions.add(mapRowToTransaction(resultSet));
            }
        }
        return transactions;
    }

    // Function to select transaction by ID
    public transactions selectTransactionById(int transactionId) throws SQLException {
        String sql = SELECT_TRANSACTION_BY_ID_SQL;
        transactions transaction = null;
        Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, transactionId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                transaction = mapRowToTransaction(resultSet);
            }
        }
        return transaction;
    }

    // Helper method to map a ResultSet row to a Transaction object
    private transactions mapRowToTransaction(ResultSet resultSet) throws SQLException {
        transactions transaction = new transactions();
        transaction.setTransactionId(resultSet.getInt("transaction_id"));
        transaction.setUsername(resultSet.getString("username"));
        transaction.setTypeTransaction(resultSet.getString("type_transaction"));
        transaction.setAmount(resultSet.getDouble("amount"));
        transaction.setDate(resultSet.getDate("date"));
        return transaction;
    }
}




