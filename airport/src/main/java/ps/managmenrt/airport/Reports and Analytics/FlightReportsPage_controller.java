package ps.managmenrt.airport;

import hostdevicedata.Flight;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField; // Change this import
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FlightReportsPage_controller {

    @FXML
    private DatePicker startDatePicker; // DatePicker for start date
    @FXML
    private DatePicker endDatePicker; // DatePicker for end date
    @FXML
    private TextField sourceTextField; // TextField for source (was ComboBox)
    @FXML
    private TextField destinationTextField; // TextField for destination (was ComboBox)
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
    public void onSearchButtonClick() {
        // Validate input fields
        if (startDatePicker.getValue() == null || endDatePicker.getValue() == null ||
                sourceTextField.getText().isEmpty() || destinationTextField.getText().isEmpty()) {
            showAlert("Input Error", "Please fill in all fields (start date, end date, source, destination).");
            return;
        }

        // Fetch the flight data based on the selected criteria
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        String source = sourceTextField.getText(); // Get text from TextField
        String destination = destinationTextField.getText(); // Get text from TextField

        List<String> flightReports = fetchFlights(startDate, endDate, source, destination);

        if (flightReports.isEmpty()) {
            showAlert("No Results", "No flights found for the selected criteria.");
        } else {
            flightsListView.getItems().clear();
            flightsListView.getItems().addAll(flightReports);
        }
    }

    // Fetch flights from the database based on selected criteria
    private List<String> fetchFlights(LocalDate startDate, LocalDate endDate, String source, String destination) {
        List<String> flightReports = new ArrayList<>();
        List<Integer> flightIds = Flight.multiplie_search(source, destination, java.sql.Date.valueOf(startDate), java.sql.Date.valueOf(endDate));

        // Query tickets based on flight IDs
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            for (Integer flightId : flightIds) {
                String query = "SELECT * FROM tickets WHERE flight_id = ?";
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setInt(1, flightId);

                    ResultSet resultSet = statement.executeQuery();

                    while (resultSet.next()) {
                        String flightReport = "Flight ID: " + resultSet.getInt("flight_id") +
                                " | Start: " + resultSet.getDate("start_date") +
                                " | End: " + resultSet.getDate("end_date");
                        flightReports.add(flightReport);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flightReports;
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
