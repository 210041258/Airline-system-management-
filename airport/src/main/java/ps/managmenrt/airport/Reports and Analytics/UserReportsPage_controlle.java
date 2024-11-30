package ps.managmenrt.airport;

import hostdevicedata.Ticket;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Button;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDate;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

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
        // Initialize any components or data here if necessary
    }

    @FXML
    private void onSearchButtonClick() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (startDate == null || endDate == null) {
            statusLabel.setText("Please select both start and end dates.");
            return;
        }



        // Get the username from the session file
        String username = getUsernameFromSessionFile();

        // Fetch and display the tickets for the user between the specified dates
        showUserTickets(username, startDate, endDate);
    }

    private void showUserTickets(String username, LocalDate startDate, LocalDate endDate) {
        // Clear the current list
        ticketsListView.getItems().clear();

        // Convert LocalDate to SQL Date for database query
        java.sql.Date sqlStartDate = java.sql.Date.valueOf(startDate);
        java.sql.Date sqlEndDate = java.sql.Date.valueOf(endDate);

        List<Ticket> tickets = Ticket.getTicketsByFlightAndDateRangeAndUsername( sqlStartDate,sqlEndDate, username);

        // Populate the ListView with fetched tickets
        if (tickets.isEmpty()) {
            statusLabel.setText("No tickets found for the selected period.");
        } else {
            for (Ticket ticket : tickets) {
                ticketsListView.getItems().add(ticket.toString());
            }
            statusLabel.setText(""); // Clear the status label
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
            stage.setTitle("User  Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getUsernameFromSessionFile() {
        String username = null;
        File directory = new File("."); // Current directory

        // Filter for files containing "_session" in their name
        File[] sessionFiles = directory.listFiles((dir, name) -> name.toLowerCase().contains("_session"));

        if (sessionFiles == null || sessionFiles.length == 0) {
            System.out.println("No session file found.");
            return null;
        }

        // Assuming only one session file is needed (first found file)
        File sessionFile = sessionFiles[0];
        System.out.println("Reading from session file: " + sessionFile.getName());

        try (BufferedReader reader = new BufferedReader(new FileReader(sessionFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Username:")) {
                    username = line.substring("Username:".length()).trim(); // Extract the username
                    break; // Exit the loop once the username is found
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return username;
    }

}