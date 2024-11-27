package hostdevicedata;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Flight {
    private int flightId;
    private String source;
    private String destination;
    private Date date;
    private Time time;
    private String owner;      
    private int planeId;       

    // JDBC connection info
    private static final String DB_URL = "jdbc:mysql://localhost:3306/user_databases";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Root@2023";

    // Path for the text file
    private static final String TEXT_FILE_PATH = "index_flight.txt";
    private static void createTableIfNotExists() {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS flights (" +
                                  "flight_id INT PRIMARY KEY, " +
                                  "source VARCHAR(255), " +
                                  "destination VARCHAR(255), " +
                                  "date DATE, " +
                                  "time TIME, " +
                                  "owner VARCHAR(255), " + // Added owner column
                                  "plane_id INT)"; // Ensure plane_id is also included
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(createTableQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // Constructor
    public Flight(int flightId, String source, String destination, Date date, Time time, String owner, int planeId) {
        createTableIfNotExists();
        this.flightId = flightId;
        this.source = source;
        this.destination = destination;
        this.date = date;
        this.time = time;
        this.owner = owner;
        this.planeId = planeId;
    }

    // Save flight to the database
    public void saveToDatabase() {
        String query = "INSERT INTO flights (flight_id, source, destination, date, time, owner, plane_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, flightId);
            statement.setString(2, source);
            statement.setString(3, destination);
            statement.setDate(4, date);
            statement.setTime(5, time);
            statement.setString(6, owner);
            statement.setInt(7, planeId);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Load all flights from the database
    public static List<Flight> loadAllFlights() {
        List<Flight> flights = new ArrayList<>();
        String query = "SELECT * FROM flights";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                flights.add(new Flight(
                        resultSet.getInt("flight_id"),
                        resultSet.getString("source"),
                        resultSet.getString("destination"),
                        resultSet.getDate("date"),
                        resultSet.getTime("time"),
                        resultSet.getString("owner"),
                        resultSet.getInt("plane_id")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flights;
    }

    // Delete flight from the database
    public void deleteFromDatabase() {
        String query = "DELETE FROM flights WHERE flight_id = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, flightId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateInDatabase(int flightId,String newSource, String newDestination, String newOwner, Integer newPlaneId, Date newDate, Time newTime) {
        String query = "UPDATE flights SET source = COALESCE(?, source), destination = COALESCE(?, destination), owner = COALESCE(?, owner), plane_id = COALESCE(?, plane_id), date = COALESCE(?, date), time = COALESCE(?, time) WHERE flight_id = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, newSource);
            statement.setString(2, newDestination);
            statement.setString(3, newOwner);
            statement.setObject(4, newPlaneId); 
            statement.setDate(5, newDate);
            statement.setTime(6, newTime);
            statement.setInt(7, flightId); 
            statement.executeUpdate();    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // Load flights from a text file
    public static List<Flight> loadFromTextFile() {
        List<Flight> flights = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(TEXT_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                flights.add(new Flight(
                        Integer.parseInt(parts[0]),  // flightId
                        parts[1],                   // source
                        parts[2],                   // destination
                        Date.valueOf(parts[3]),     // date
                        Time.valueOf(parts[4]),     // time
                        parts[5],                   // owner
                        Integer.parseInt(parts[6])  // planeId
                ));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flights;
    }

    // Store flights to a text file
    public static void storeToTextFile(List<Flight> flights) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(TEXT_FILE_PATH))) {
            for (Flight flight : flights) {
                writer.println(flight.flightId + "," + flight.source + "," + flight.destination + "," + flight.date + "," + flight.time + "," + flight.owner + "," + flight.planeId);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load flights from database and write to a text file
    public static void loadFromDatabaseToTextFile() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM flights");
             PrintWriter writer = new PrintWriter(new FileWriter(TEXT_FILE_PATH))) {

            while (resultSet.next()) {
                writer.println(resultSet.getInt("flight_id") + "," +
                        resultSet.getString("source") + "," +
                        resultSet.getString("destination") + "," +
                        resultSet.getDate("date") + "," +
                        resultSet.getTime("time") + "," +
                        resultSet.getString("owner") + "," +
                        resultSet.getInt("plane_id"));
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    // Store flights from a text file to the database
    public static void storeFromTextFileToDatabase() {
        try (BufferedReader reader = new BufferedReader(new FileReader(TEXT_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                Flight flight = new Flight(
                        Integer.parseInt(parts[0]), 
                        parts[1],                   
                        parts[2],                   
                        Date.valueOf(parts[3]),     
                        Time.valueOf(parts[4]),     
                        parts[5],                   
                        Integer.parseInt(parts[6])  
                );
                flight.saveToDatabase(); 
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void autoUpdateDatabase() {
        updateInDatabase(
            this.flightId,
            this.source,
            this.destination,
            this.owner,
            this.planeId,
            this.date,
            this.time
        );
    }
    
    public void setSource(String source) {
        this.source = source;
        autoUpdateDatabase();
    }
    
    public void setDestination(String destination) {
        this.destination = destination;
        autoUpdateDatabase();
    }
    
    public void setOwner(String owner) {
        this.owner = owner;
        autoUpdateDatabase();
    }
    
    public void setPlaneId(int planeId) {
        this.planeId = planeId;
        autoUpdateDatabase();
    }
    
    public void setDate(Date date) {
        this.date = date;
        autoUpdateDatabase();
    }
    
    public void setTime(Time time) {
        this.time = time;
        autoUpdateDatabase();
    }
    // Getters
    public int getFlightId() {
        return flightId;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public Date getDate() {
        return date;
    }

    public Time getTime() {
        return time;
    }

    public String getOwner() {
        return owner;
    }

    public int getPlaneId() {
        return planeId;
    }

    public static List<Integer> searchBySourceAndDestination(String source, String destination) {
        List<Integer> flightIds = new ArrayList<>();
        String query = "SELECT flight_id FROM flights WHERE source = ? AND destination = ?";
        
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, source);
            statement.setString(2, destination);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    flightIds.add(resultSet.getInt("flight_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flightIds;
    }


    public static List<Integer> searchByDateRange(Date startDate, Date endDate) {
        List<Integer> flightIds = new ArrayList<>();
        String query = "SELECT flight_id FROM flights WHERE date BETWEEN ? AND ?";
        
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDate(1, startDate);
            statement.setDate(2, endDate);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    flightIds.add(resultSet.getInt("flight_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flightIds;
    }

    public static List<Integer> multiplie_search(String source, String destination, Date startDate, Date endDate) {
        List<Integer> flightIdsBySourceAndDestination = searchBySourceAndDestination(source, destination);

        // Search by date range
        List<Integer> flightIdsByDate = searchByDateRange(startDate, endDate);

        // Find common flight IDs
        List<Integer> commonFlightIds = new ArrayList<>();

        // Iterate through the first list and check if the element is in the second list
        for (Integer flightId : flightIdsBySourceAndDestination) {
            if (flightIdsByDate.contains(flightId)) {
                commonFlightIds.add(flightId);
            }
        }

        return commonFlightIds;
    }

}
