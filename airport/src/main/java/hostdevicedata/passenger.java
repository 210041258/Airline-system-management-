package ps.managmenrt.airport;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Passenger {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/user_databases";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Root@2023";

    private int id;
    private String username;
    private String password;
    private String email;
    private double balance;

    // Constructor
    public Passenger(int id, String username, String password, String email, double balance) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.balance = balance;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        updatePassengerInDB("username", username);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        updatePassengerInDB("password", password);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        updatePassengerInDB("email", email);
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
        updatePassengerInDB("balance", String.valueOf(balance));
    }

    // Method to create the passengers table if it does not exist
    public static void createPassengerTable() {
        String createPassengerTableSQL = "CREATE TABLE IF NOT EXISTS passengers (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "username VARCHAR(50) NOT NULL UNIQUE, " +
                "password VARCHAR(100) NOT NULL, " +
                "email VARCHAR(100) NOT NULL, " +
                "balance DOUBLE NOT NULL" +
                ")";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(createPassengerTableSQL);
            System.out.println("Passenger table created or already exists.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to insert or update a passenger record in the database
    private void updatePassengerInDB(String field, String newValue) {
        String updateSQL = "UPDATE passengers SET " + field + " = ? WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {
            preparedStatement.setString(1, newValue);
            preparedStatement.setInt(2, this.id);
            preparedStatement.executeUpdate();
            System.out.println("Passenger " + field + " updated to " + newValue + " in the database.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to insert a new passenger or update an existing one
    public static void insertOrUpdatePassengerToDB(Passenger passenger) {
        String checkIfExistsSQL = "SELECT COUNT(*) FROM passengers WHERE username = ?";
        String updateSQL = "UPDATE passengers SET " +
                           "password = COALESCE(?, password), " +
                           "email = COALESCE(?, email), " +
                           "balance = COALESCE(?, balance) " +
                           "WHERE username = ?";
        String insertSQL = "INSERT INTO passengers (username, password, email, balance) VALUES (?, ?, ?, ?)";
    
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Check if the passenger already exists
            try (PreparedStatement checkStatement = connection.prepareStatement(checkIfExistsSQL)) {
                checkStatement.setString(1, passenger.getUsername());
                ResultSet resultSet = checkStatement.executeQuery();
                resultSet.next();
                int count = resultSet.getInt(1);
    
                // If the passenger exists, update it; otherwise, insert a new record
                if (count > 0) {
                    try (PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {
                        // Set parameters for COALESCE
                        preparedStatement.setString(1, passenger.getPassword());
                        preparedStatement.setString(2, passenger.getEmail());
                        preparedStatement.setDouble(3, passenger.getBalance());
                        preparedStatement.setString(4, passenger.getUsername()); // Set username last
                        preparedStatement.executeUpdate();
                        System.out.println("Passenger record updated.");
                    }
                } else {
                    try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
                        preparedStatement.setString(1, passenger.getUsername());
                        preparedStatement.setString(2, passenger.getPassword());
                        preparedStatement.setString(3, passenger.getEmail());
                        preparedStatement.setDouble(4, passenger.getBalance());
                        preparedStatement.executeUpdate();
                        System.out.println("Passenger record inserted.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to delete a passenger record by username
    public static void deletePassengerByUsername(String username) {
        String deleteSQL = "DELETE FROM passengers WHERE username = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL)) {
            preparedStatement.setString(1, username);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Passenger deleted.");
            } else {
                System.out.println("Passenger with username " + username + " not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to search for a passenger by username
    public static Passenger searchByUsername(String username) {
        String query = "SELECT * FROM passengers WHERE username = ?";
        Passenger passenger = null;

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                passenger = new Passenger(
                        resultSet.getInt("id"),
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("email"),
                        resultSet.getDouble("balance")
                );
                System.out.println("Passenger found: " + passenger.getUsername());
            } else {
                System.out.println("Passenger with username " + username + " not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return passenger;
    }

    // Method to search for passengers by balance range
    public static List<Passenger> searchByBalanceRange(double minBalance, double maxBalance) {
        String query = "SELECT * FROM passengers WHERE balance BETWEEN ? AND ?";
        List<Passenger> passengerList = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setDouble(1, minBalance);
            preparedStatement.setDouble(2, maxBalance);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Passenger passenger = new Passenger(
                        resultSet.getInt("id"),
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("email"),
                        resultSet.getDouble("balance")
                );
                passengerList.add(passenger);
            }

            if (passengerList.isEmpty()) {
                System.out.println("No passengers found in the balance range.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return passengerList;
    }

    public static void storeToFile(List<Passenger> passengerList, String filename) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (Passenger passenger : passengerList) {
                String line = passenger.getId() + "," +
                        passenger.getUsername() + "," +
                        passenger.getPassword() + "," +
                        passenger.getEmail() + "," +
                        passenger.getBalance();
                bw.write(line);
                bw.newLine();
            }
            System.out.println("Passenger data stored to " + filename + " successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to load passengers from a text file and store them in the database
    public static void loadPassengersFromFile(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 5) {
                    int id = Integer.parseInt(data[0].trim());
                    String username = data[1].trim();
                    String password = data[2].trim();
                    String email = data[3].trim();
                    double balance = Double.parseDouble(data[4].trim());

                    Passenger passenger = new Passenger(id, username, password, email, balance);
                    insertOrUpdatePassengerToDB(passenger);
                }
            }
            System.out.println("Passengers loaded from " + filename + " successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to retrieve all passengers from the database
    public static List<Passenger> getAllPassengers() {
        String query = "SELECT * FROM passengers";
        List<Passenger> passengerList = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Passenger passenger = new Passenger(
                        resultSet.getInt("id"),
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("email"),
                        resultSet.getDouble("balance")
                );
                passengerList.add(passenger);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return passengerList;
    }

    // Method to load and store passengers from a text file to the database and back to a text file
    public static void loadAndStorePassengers(String loadFilename, String storeFilename) {
        // Load passengers from text file into the database
        loadPassengersFromFile(loadFilename);

        // Retrieve all passengers from the database
        List<Passenger> passengers = getAllPassengers();

        // Store passengers into a text file
        storeToFile(passengers, storeFilename);
    }


}