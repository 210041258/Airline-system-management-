package hostdevicedata;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class reports {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/user_databases";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Root@2023";


    public reports() {
        createTables();
    }


    private void createTables() {
        String createTicketsTableSQL = "CREATE TABLE IF NOT EXISTS tickets (" +
                "ticket_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "flight_id INT NOT NULL, " +
                "start_date DATE NOT NULL, " +
                "end_date DATE NOT NULL, " +
                "price DOUBLE NOT NULL" +
                ")";

        String createTransactionsTableSQL = "CREATE TABLE IF NOT EXISTS transactions (" +
                "transaction_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "username VARCHAR(50) NOT NULL, " +
                "amount DECIMAL(10, 2) NOT NULL, " +
                "date DATE NOT NULL, " +
                "type_transaction VARCHAR(50) NOT NULL, " +
                "transaction_message TEXT" +
                ")";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(createTicketsTableSQL);
            statement.executeUpdate(createTransactionsTableSQL);
            System.out.println("Tables created or already exist.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public List<String> generateTicketReport(int flightId) {
        List<String> report = new ArrayList<>();
        String query = "SELECT * FROM tickets WHERE flight_id = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, flightId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String ticketDetails = String.format("Ticket ID: %d, Flight ID: %d, Start Date: %s, End Date: %s, Price: %.2f",
                        resultSet.getInt("ticket_id"),
                        resultSet.getInt("flight_id"),
                        resultSet.getDate("start_date"),
                        resultSet.getDate("end_date"),
                        resultSet.getDouble("price"));
                report.add(ticketDetails);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return report;
    }


    public void displayReport(List<String> report) {
        if (report.isEmpty()) {
            System.out.println("No records found.");
        } else {
            for (String record : report) {
                System.out.println(record);
            }
        }
    }
    

    public List<String> getReportLines(List<String> report) {
        if (report.isEmpty()) {
            report.add("No records found.");
        }
        return report;
    }


    public void loadFromFile() {
        String insertTicketSQL = "INSERT INTO tickets (flight_id, start_date, end_date, price) VALUES (?, ?, ?, ?)";
        try (BufferedReader br = new BufferedReader(new FileReader("report_index.txt"));
             Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(insertTicketSQL)) {

            String line;
            while ((line = br.readLine()) != null) {
                // Assuming the file has the format: flight_id,start_date,end_date,price
                String[] data = line.split(",");
                if (data.length == 4) {
                    preparedStatement.setInt(1, Integer.parseInt(data[0]));
                    preparedStatement.setDate(2, Date.valueOf(data[1])); // Assuming format is YYYY-MM-DD
                    preparedStatement.setDate(3, Date.valueOf(data[2]));
                    preparedStatement.setDouble(4, Double.parseDouble(data[3]));
                    preparedStatement.executeUpdate();
                }
            }
            System.out.println("Data loaded from report_index.txt successfully.");
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadFromDatabase() {
        String query = "SELECT * FROM tickets";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                String ticketDetails = String.format("Ticket ID: %d, Flight ID: %d, Start Date: %s, End Date: %s, Price: %.2f",
                        resultSet.getInt("ticket_id"),
                        resultSet.getInt("flight_id"),
                        resultSet.getDate("start_date"),
                        resultSet.getDate("end_date"),
                        resultSet.getDouble("price"));
                System.out.println(ticketDetails);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
