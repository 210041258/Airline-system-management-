package ps.managmenrt.airport;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FlightReportsPage_controller {

    @FXML
    private DatePicker startDatePicker; // DatePicker for start date
    @FXML
    private DatePicker endDatePicker; // DatePicker for end date
    @FXML
    private ComboBox<String> destinationComboBox; // ComboBox for destination
    @FXML
    private ComboBox<String> sourceComboBox; // ComboBox for source
    @FXML
    private ListView<String> flightsListView; // ListView to display flight reports
    @FXML
    private Label statusLabel; // Label for status messages
    @FXML
    private Button searchButton; // Button to trigger the search

    // Database credentials
    private static final String DB_URL = "jdbc:mysql://localhost:3306/user_databases";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Root@2023";

    @FXML
    public void initialize() {
        loadDestinations();
        loadSources();
    }

    // Load destinations into the ComboBox
    private void loadDestinations() {
        // Populate destinationComboBox with available destinations from the database
        // Example: destinationComboBox.getItems().addAll("New York", "London", "Tokyo");
    }

    // Load sources into the ComboBox
    private void loadSources() {
        // Populate sourceComboBox with available sources from the database
        // Example: sourceComboBox.getItems().addAll("Los Angeles", "Paris", "Dubai");
    }

    @FXML
    public void onSearchButtonClick() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        String selectedDestination = destinationComboBox.getValue();
        String selectedSource = sourceComboBox.getValue();

        if (startDate == null || endDate == null) {
            showAlert("Date Error", "Please select both start and end dates.");
            return;
        }

        // Fetch and display flights based on selected criteria
        List<String> flights = fetchFlights(startDate, endDate, selectedSource, selectedDestination);
        flightsListView.getItems().clear();
        flightsListView.getItems().addAll(flights);

        statusLabel.setText(flights.isEmpty() ? "No flights found." : "Flights retrieved successfully.");
    }

    // Fetch flights from the database based on selected criteria
    private List<String> fetchFlights(LocalDate startDate, LocalDate endDate, String source, String destination) {
        List<String> flights = new ArrayList<>();
        String query = "SELECT flight_id, source, destination, date FROM flights WHERE date BETWEEN ? AND ?";

        if (source != null && !source.isEmpty()) {
            query += " AND source = ?";
        }
        if (destination != null && !destination.isEmpty()) {
            query += " AND destination = ?";
        }

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDate(1, java.sql.Date.valueOf(startDate));
            statement.setDate(2, java.sql.Date.valueOf(endDate));

            int index = 3;
            if (source != null && !source.isEmpty()) {
                statement.setString(index++, source);
            }
            if (destination != null && !destination.isEmpty()) {
                statement.setString(index++, destination);
            }

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String flightInfo = String.format("Flight ID: %s, Source: %s, Destination: %s, Date: %s",
                        resultSet.getString("flight_id"),
                        resultSet.getString("source"),
                        resultSet.getString("destination"),
                        resultSet.getDate("date"));
                flights.add(flightInfo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to fetch flight data.");
        }
        return flights;
    }

    // Show alert dialog
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard/AdminDashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) startDatePicker.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Flights Management");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
