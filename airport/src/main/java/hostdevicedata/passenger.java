package hostdevicedata;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class passenger {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/user_databases";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Root@2023";

    private int id;
    private String username;
    private String password;
    private String email;
    private double balance;

    // Constructor for creating a new passenger with the id
    public passenger(int id, String username, String password, String email, double balance) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.balance = balance;
    }

    // Constructor for creating a passenger without id
    public passenger(String username, String password, String email, double balance) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.balance = balance;
        createpassengerTable();
    }
    public  passenger() {};
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
        updatepassengerInDB("username", username);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        updatepassengerInDB("email", email);
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
        updatepassengerInDB("balance", String.valueOf(balance));
    }

    // Method to create the passengers table if it does not exist
    public static void createpassengerTable() {
        String createpassengerTableSQL = """

                CREATE TABLE IF NOT EXISTS passengers (
    id INT AUTO_INCREMENT PRIMARY KEY,       
    username VARCHAR(50) NOT NULL,           
    balance DOUBLE NOT NULL DEFAULT 0.0,                  
    email VARCHAR(100) NOT NULL,           
    FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
);
""";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(createpassengerTableSQL);
            System.out.println("passenger table created or already exists.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public void updateUserAndPassenger(String username, double balance, String email,String password1) {

        // Prepare the stored procedure call
        String query = "{CALL UpdateUserAndPassenger(?, ?, ?, ?)}";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {

            // Now call the stored procedure to update user and passenger data
            try (CallableStatement stmt = connection.prepareCall(query)) {
                // Set parameters
                stmt.setString(1, username);
                stmt.setDouble(2, balance);
                stmt.setString(3, email);
                stmt.setString(4, password1);  // Pass the current password

                stmt.executeUpdate();
                System.out.println("User and passenger data updated successfully.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error updating user and passenger.");
        }
    }


    public static void updateBalance(String username, double newBalance) {
        String updateSQL = "UPDATE users SET balance = ? WHERE username = ?";
    
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {
            preparedStatement.setDouble(1, newBalance);
            preparedStatement.setString(2, username);
            int rowsAffected = preparedStatement.executeUpdate();
    
            if (rowsAffected > 0) {
                System.out.println("Balance updated successfully for user: " + username);
            } else {
                System.out.println("No passenger found with username: " + username);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static double getBalanceByUsername(String username) {
        double balance = 0.0; // Default balance if user is not found
        String query = "SELECT balance FROM passengers WHERE username = ?";
    
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
    
            if (resultSet.next()) {
                balance = resultSet.getDouble("balance"); // Retrieve the balance
                System.out.println("Balance retrieved for user " + username + ": " + balance);
            } else {
                System.out.println("No passenger found with username: " + username);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return balance; // Return the balance (0.0 if not found)
    }


    // Method to insert or update a passenger record in the database
    private void updatepassengerInDB(String field, String newValue) {
        String updateSQL = "UPDATE passengers SET " + field + " = ? WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {
            preparedStatement.setString(1, newValue);
            preparedStatement.setInt(2, this.id);
            preparedStatement.executeUpdate();
            System.out.println("passenger " + field + " updated to " + newValue + " in the database.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to insert a new passenger or update an existing one
    public static void insertOrUpdatepassengerToDB(passenger passenger) {
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
                        preparedStatement.setString(1, passenger.getPassword());
                        preparedStatement.setString(2, passenger.getEmail());
                        preparedStatement.setDouble(3, passenger.getBalance());
                        preparedStatement.setString(4, passenger.getUsername()); // Set username last
                        preparedStatement.executeUpdate();
                        System.out.println("passenger record updated.");
                    }
                } else {
                    try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
                        preparedStatement.setString(1, passenger.getUsername());
                        preparedStatement.setString(2, passenger.getPassword());
                        preparedStatement.setString(3, passenger.getEmail());
                        preparedStatement.setDouble(4, passenger.getBalance());
                        preparedStatement.executeUpdate();
                        System.out.println("passenger record inserted.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to delete a passenger record by username
    public static void deletepassengerByUsername(String username) {
        String deleteSQL = "DELETE FROM passengers WHERE username = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL)) {
            preparedStatement.setString(1, username);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("passenger deleted.");
            } else {
                System.out.println("passenger with username " + username + " not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public static passenger searchByUsername(String username) {
        String selectPasswordQuery = "SELECT password FROM users WHERE username = ?";
        passenger passenger = null;
        String password = null;


        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(selectPasswordQuery)) {

            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                password = resultSet.getString("password");  // Correctly fetch the password
            } else {
                return null;  // If no user is found, return null
            }
        } catch (SQLException e) {
            return null;  // Return null if any exception occurs
        }

        // Now fetch the passenger details using the password
        String query = "SELECT * FROM passengers WHERE username = ?";
        try (PreparedStatement preparedStatement = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD).prepareStatement(query)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                passenger = new passenger(
                        resultSet.getInt("id"),
                        resultSet.getString("username"),
                        password,  // Use the fetched password
                        resultSet.getString("email"),
                        resultSet.getDouble("balance")
                );
            } else {
            }
        } catch (SQLException e) {
        }

        return passenger;
    }



    // Method to retrieve all passengers from the database
    public static List<passenger> getAllpassengers() {
        String query = "SELECT * FROM passengers";
        List<passenger> passengerList = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                passenger passenger = new passenger(
                        resultSet.getInt("id"),
                        resultSet.getString("username"),
                        "hidden",
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

    // Method to store passenger data to a file
    public static void storeToFile(List<passenger> passengerList, String filename) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (passenger passenger : passengerList) {
                String line = passenger.getId() + "," +
                        passenger.getUsername() + "," +
                        passenger.getPassword() + "," +
                        passenger.getEmail() + "," +
                        passenger.getBalance();
                bw.write(line);
                bw.newLine();
            }
            System.out.println("passenger data stored to " + filename + " successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to load passengers from a text file and store them in the database
    public static void loadpassengersFromFile(String filename) {
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

                    passenger passenger = new passenger(id, username, password, email, balance);
                    insertOrUpdatepassengerToDB(passenger);
                }
            }
            System.out.println("passengers loaded from " + filename + " successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
