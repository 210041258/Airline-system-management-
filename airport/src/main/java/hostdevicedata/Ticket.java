package hostdevicedata;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Ticket {
    private int ticketId;
    private int flightId;
    private String passengerName;
    private Date date;
    private double price;

    // JDBC connection info
    private static final String DB_URL = "jdbc:mysql://localhost:3306/user_databases";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Root@2023";

    public Ticket(int ticketId, int flightId, String passengerName, Date date, double price) {
        this.ticketId = ticketId;
        this.flightId = flightId;
        this.passengerName = passengerName;
        this.date = date;
        this.price = price;
    }

    // Method to save ticket to database
    public void saveToDatabase() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO tickets (flight_id, passenger_name, date, price) VALUES (?, ?, ?, ?)");
            statement.setInt(1, flightId);
            statement.setString(2, passengerName);
            statement.setDate(3, date);
            statement.setDouble(4, price);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to load all tickets for a specific flight
    public static List<Ticket> loadTicketsForFlight(int flightId) {
        List<Ticket> tickets = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM tickets WHERE flight_id = ?");
            statement.setInt(1, flightId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Ticket ticket = new Ticket(
                        resultSet.getInt("ticket_id"),
                        resultSet.getInt("flight_id"),
                        resultSet.getString("passenger_name"),
                        resultSet.getDate("date"),
                        resultSet.getDouble("price")
                );
                tickets.add(ticket);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tickets;
    }


    public void updateInDatabase() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE tickets SET flight_id = ?, passenger_name = ?, date = ?, price = ? WHERE ticket_id = ?");
            statement.setInt(1, flightId);
            statement.setDate(3, date);
            statement.setDouble(4, price);
            statement.setInt(5, ticketId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Method to delete a ticket from database
    public void deleteFromDatabase() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM tickets WHERE ticket_id = ?");
            statement.setInt(1, ticketId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
