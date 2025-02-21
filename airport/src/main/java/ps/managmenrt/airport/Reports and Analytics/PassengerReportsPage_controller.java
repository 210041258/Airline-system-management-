package ps.managmenrt.airport;

import hostdevicedata.Ticket;
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
import java.time.LocalDate;
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

    @FXML
    private void onSearchButtonClick() {
        // Get the selected dates and username
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        String username = usernameField.getText();

        // Validate input
        if (startDate == null || endDate == null || username.isEmpty()) {
            statusLabel.setText("Please fill in all fields.");
            return;
        }

        // Call the method to fetch and display the tickets based on user input
        showUserTickets(username, startDate, endDate);
    }

    private void showUserTickets(String username, LocalDate startDate, LocalDate endDate) {
        // Clear the current list
        ticketsListView.getItems().clear();

        // Convert LocalDate to SQL Date for database query
        java.sql.Date sqlStartDate = java.sql.Date.valueOf(startDate);
        java.sql.Date sqlEndDate = java.sql.Date.valueOf(endDate);

        // Fetch the tickets based on the username and date range
        List<Ticket> tickets = Ticket.getTicketsByFlightAndDateRangeAndUsername(sqlStartDate, sqlEndDate, username);

        // Populate the ListView with fetched tickets
        if (tickets.isEmpty()) {
            statusLabel.setText("No tickets found for the selected period.");
        } else {
            for (Ticket ticket : tickets) {
                ticketsListView.getItems().add(ticket.toString()); // Assuming Ticket class has a proper toString method
            }
            statusLabel.setText(""); // Clear the status label
        }
    }

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
