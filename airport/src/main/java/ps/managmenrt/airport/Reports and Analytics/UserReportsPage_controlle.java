package ps.managmenrt.airport;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Button;
import java.time.LocalDate;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class UserReportsPage_controlle {

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private Label statusLabel;

    @FXML
    private ListView<String> ticketsListView;

    @FXML
    private Button showTicketsButton;

    @FXML
    public void initialize() {
        // You can initialize any components or data here if necessary
    }

    @FXML
    private void onSearchButtonClick() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (startDate != null && endDate != null) {
            if (startDate.isBefore(endDate)) {
                // Assuming you have a method to fetch tickets for the logged-in user
                //String username = SessionData.getCurrentUser().getUsername(); // Get the current username
                //showUserTickets(username, startDate, endDate);
            } else {
                statusLabel.setText("End date must be after start date.");
            }
        } else {
            statusLabel.setText("Please select both start and end dates.");
        }
    }

    private void showUserTickets(String username, LocalDate startDate, LocalDate endDate) {
        // Clear the current list
        ticketsListView.getItems().clear();

        // Fetch the tickets for the user based on the provided date range
        // This is a placeholder for your actual data fetching logic
        // Replace with actual logic to retrieve tickets from your data source
        ///for (String ticket : TicketService.getUserTickets(username, startDate, endDate)) {
        //    ticketsListView.getItems().add(ticket);
       // }

        if (ticketsListView.getItems().isEmpty()) {
            statusLabel.setText("No tickets found for the selected period.");
        } else {
            statusLabel.setText(""); // Clear any previous status message
        }
    }

    @FXML
    public void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard/UserDashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) startDatePicker.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("User Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }    }
}

