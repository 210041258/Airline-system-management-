package hostdevicedata;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Ticket {
    private int ticketId;
    private int flightId;
    private Date startDate;
    private Date endDate;
    private double price;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/user_databases";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Root@2023";
    private static final String FILE_PATH = "ticket_index.txt"; 

    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String show_db_sql_by_flight = "SELECT * FROM ticket WHERE flight_id = ?";
    private static final String show_db_sql_by_ticket = "SELECT * FROM ticket WHERE ticket_id = ?";
    private static final String show_db_sql_by_flight_start = "SELECT * FROM ticket WHERE ticket_id = ? AND start_date = ?";
    private static final String show_db_sql_by_flight_end = "SELECT * FROM ticket WHERE ticket_id = ? AND end_date = ?";
    private static final String show_db_sql_by_flight_start_end = "SELECT * FROM ticket WHERE ticket_id = ? AND start_date BETWEEN ? AND ? AND end_date BETWEEN ? AND ?";
    private static final String show_db_sql_by_flight_start_end_ticket = "SELECT * FROM ticket WHERE start_date = ? AND end_date = ? AND ticket_id = ? ";
    

    public static Ticket getTicketByFlightAndDateRangeAndTicketId(Date startDate, Date endDate, int ticketId) {
        Ticket ticket = null;
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(show_db_sql_by_flight_start_end_ticket)) {
            
            statement.setDate(1, startDate);
            statement.setDate(2, endDate);
            statement.setInt(3, ticketId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                ticket = new Ticket(
                        resultSet.getInt("ticket_id"),
                        resultSet.getInt("flight_id"),
                        resultSet.getDate("start_date"),
                        resultSet.getDate("end_date"),
                        resultSet.getDouble("price")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ticket;
    }
    

    public static List<Ticket> getTicketsByFlightAndDateRange(int ticketId, Date startDate, Date endDate) {
        List<Ticket> tickets = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(show_db_sql_by_flight_start_end)) {
            
            statement.setInt(1, ticketId);
            statement.setDate(2, startDate);
            statement.setDate(3, endDate);
            statement.setDate(4, startDate);  // Same date for both start and end range
            statement.setDate(5, endDate);    // Same date for both start and end range
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Ticket ticket = new Ticket(
                        resultSet.getInt("ticket_id"),
                        resultSet.getInt("flight_id"),
                        resultSet.getDate("start_date"),
                        resultSet.getDate("end_date"),
                        resultSet.getDouble("price")
                );
                tickets.add(ticket);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tickets;
    }

    public static List<Ticket> getTicketsByFlightAndEndDate(int ticketId, Date endDate) {
        List<Ticket> tickets = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(show_db_sql_by_flight_end)) {
            
            statement.setInt(1, ticketId);
            statement.setDate(2, endDate);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Ticket ticket = new Ticket(
                        resultSet.getInt("ticket_id"),
                        resultSet.getInt("flight_id"),
                        resultSet.getDate("start_date"),
                        resultSet.getDate("end_date"),
                        resultSet.getDouble("price")
                );
                tickets.add(ticket);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tickets;
    }

    public static List<Ticket> getTicketsByFlightAndStartDate(int ticketId, Date startDate) {
        List<Ticket> tickets = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(show_db_sql_by_flight_start)) {
            
            statement.setInt(1, ticketId);
            statement.setDate(2, startDate);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Ticket ticket = new Ticket(
                        resultSet.getInt("ticket_id"),
                        resultSet.getInt("flight_id"),
                        resultSet.getDate("start_date"),
                        resultSet.getDate("end_date"),
                        resultSet.getDouble("price")
                );
                tickets.add(ticket);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tickets;
    }
  
    public static Ticket getTicketByTicketId(int ticketId) {
        Ticket ticket = null;
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(show_db_sql_by_ticket)) {
            
            statement.setInt(1, ticketId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                ticket = new Ticket(
                        resultSet.getInt("ticket_id"),
                        resultSet.getInt("flight_id"),
                        resultSet.getDate("start_date"),
                        resultSet.getDate("end_date"),
                        resultSet.getDouble("price")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ticket;
    }


    public static List<Ticket> getTicketsByFlight(int flightId) {
        List<Ticket> tickets = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(show_db_sql_by_flight)) {
            
            statement.setInt(1, flightId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Ticket ticket = new Ticket(
                        resultSet.getInt("ticket_id"),
                        resultSet.getInt("flight_id"),
                        resultSet.getDate("start_date"),
                        resultSet.getDate("end_date"),
                        resultSet.getDouble("price")
                );
                tickets.add(ticket);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tickets;
    }
    




    public Ticket(int ticketId, int flightId, Date startDate, Date endDate, double price) {
        createTable();
        this.ticketId = ticketId;
        this.flightId = flightId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.price = price;
    }

    public static void createTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS tickets ("
        + "ticket_id INT AUTO_INCREMENT PRIMARY KEY, "
        + "flight_id INT NOT NULL, "
        + "start_date DATE NOT NULL, "
        + "end_date DATE NOT NULL, "
        + "price DOUBLE NOT NULL, "
        + "FOREIGN KEY (flight_id) REFERENCES flights(flight_id)"
        + ")";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statement = connection.createStatement()) {

            statement.executeUpdate(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveToDatabase() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO tickets (flight_id, start_date, end_date, price) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            statement.setInt(1, flightId);
            statement.setDate(2, startDate);
            statement.setDate(3, endDate);
            statement.setDouble(4, price);
            statement.executeUpdate();

            ResultSet keys = statement.getGeneratedKeys();
            if (keys.next()) {
                this.ticketId = keys.getInt(1);
            }

            updateTextFile(); 
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateInDatabase(Integer flightId, java.sql.Date startDate, java.sql.Date endDate, Double price, int ticketId) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE tickets SET flight_id = COALESCE(?, flight_id), " +
                    "start_date = COALESCE(?, start_date), " +
                    "end_date = COALESCE(?, end_date), " +
                    "price = COALESCE(?, price) " +
                    "WHERE ticket_id = ?"
            );
            statement.setObject(1, flightId); // Set flightId, can be null
            statement.setObject(2, startDate); // Set startDate, can be null
            statement.setObject(3, endDate); // Set endDate, can be null
            statement.setObject(4, price); // Set price, can be null
            statement.setInt(5, ticketId); // Set ticketId, must be non-null
    
            statement.executeUpdate();
    
            updateTextFile(); 
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteFromDatabase() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM tickets WHERE ticket_id = ?");
            statement.setInt(1, ticketId);
            statement.executeUpdate();
            updateTextFile();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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
                        resultSet.getDate("start_date"),
                        resultSet.getDate("end_date"),
                        resultSet.getDouble("price")
                );
                tickets.add(ticket);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tickets;
    }

    private static void updateTextFile() {
        List<Ticket> tickets = loadAllTickets();
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            writer.println("ticket_id,flight_id,start_date,end_date,price"); // Header
            for (Ticket ticket : tickets) {
                writer.printf("%d,%d,%s,%s,%.2f%n",
                        ticket.getTicketId(),
                        ticket.getFlightId(),
                        ticket.getStartDate(),
                        ticket.getEndDate(),
                        ticket.getPrice()
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Ticket> loadAllTickets() {
        List<Ticket> tickets = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM tickets");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Ticket ticket = new Ticket(
                        resultSet.getInt("ticket_id"),
                        resultSet.getInt("flight_id"),
                        resultSet.getDate("start_date"),
                        resultSet.getDate("end_date"),
                        resultSet.getDouble("price")
                );
                tickets.add(ticket);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tickets;
    }


    private void autoUpdateDatabase() {
        updateInDatabase(
            this.flightId,
            new java.sql.Date(this.startDate.getTime()),
            new java.sql.Date(this.endDate.getTime()),
            this.price,
            this.ticketId
        );
    }


    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public int getFlightId() {
        return flightId;
    }

    public void setFlightId(int flightId) {
        this.flightId = flightId;
        autoUpdateDatabase();
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
        autoUpdateDatabase();
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
        autoUpdateDatabase();
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
        autoUpdateDatabase();
    }

}
