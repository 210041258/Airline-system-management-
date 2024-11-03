package ps.managmenrt.airport;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class PassengerReportsPage_controller {

    @FXML
    private DatePicker startDatePicker; // Start date picker for filtering
    @FXML
    private DatePicker endDatePicker; // End date picker for filtering
    @FXML
    private TextField usernameField; // Input field for passenger username
    @FXML
    private ListView<String> ticketsListView; // List view to display tickets
    @FXML
    private Label statusLabel; // Label for status messages

    /**
     * Method to handle search button click to fetch passenger tickets.
     */
    @FXML
    private void onSearchButtonClick() {
        // Get the selected dates
        var startDate = startDatePicker.getValue();
        var endDate = endDatePicker.getValue();
        String username = usernameField.getText();

        // Validate input
        if (startDate == null || endDate == null || username.isEmpty()) {
            statusLabel.setText("Please fill in all fields.");
            return;
        }

        // Fetch tickets based on username and date range
        List<String> tickets = fetchTickets(username, startDate, endDate);

        // Update the ListView with fetched tickets
        if (tickets.isEmpty()) {
            statusLabel.setText("No tickets found for the specified criteria.");
        } else {
            ticketsListView.getItems().clear(); // Clear previous items
            ticketsListView.getItems().addAll(tickets); // Add new tickets
            statusLabel.setText("Tickets fetched successfully.");
        }
    }

    /**
     * Simulated method to fetch tickets from the database or other data source.
     *
     * @param username The username of the passenger.
     * @param startDate The start date for the search period.
     * @param endDate The end date for the search period.
     * @return List of tickets matching the criteria.
     */
    private List<String> fetchTickets(String username, java.time.LocalDate startDate, java.time.LocalDate endDate) {
        // Placeholder for ticket retrieval logic. In a real application, this should
        // query the database or an API to get the tickets for the specified user and dates.

        // Example implementation, replace with actual logic.
        return List.of("Ticket1 for " + username, "Ticket2 for " + username);
    }

    /**
     * Method to handle back button action.
     */
    @FXML
    private void goBack() {
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
