package hostdevicedata;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Flight {
    private int flightId;
    private String source;
    private String destination;
    private Date date;
    private Time time;

    // JDBC connection info
    private static final String DB_URL = "jdbc:mysql://localhost:3306/user_databases";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Root@2023";

    public Flight(int flightId, String source, String destination, Date date, Time time) {
        this.flightId = flightId;
        this.source = source;
        this.destination = destination;
        this.date = date;
        this.time = time;
    }

    // Method to save flight to database
    public void saveToDatabase() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO flights (source, destination, date, time) VALUES (?, ?, ?, ?)");
            statement.setString(1, source);
            statement.setString(2, destination);
            statement.setDate(3, date);
            statement.setTime(4, time);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to load all flights from database
    public static List<Flight> loadAllFlights() {
        List<Flight> flights = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM flights");
            while (resultSet.next()) {
                Flight flight = new Flight(
                        resultSet.getInt("flight_id"),
                        resultSet.getString("source"),
                        resultSet.getString("destination"),
                        resultSet.getDate("date"),
                        resultSet.getTime("time")
                );
                flights.add(flight);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flights;
    }

    // Method to delete a flight from database
    public void deleteFromDatabase() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM flights WHERE flight_id = ?");
            statement.setInt(1, flightId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Update flight in database
    public void updateInDatabase() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE flights SET source = ?, destination = ?, date = ?, time = ? WHERE flight_id = ?");
            statement.setString(1, source);
            statement.setString(2, destination);
            statement.setDate(3, date);
            statement.setTime(4, time);
            statement.setInt(5, flightId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
