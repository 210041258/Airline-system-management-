package ps.managmenrt.airport;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import java.time.LocalDate;
import java.io.IOException;

public class CancelBookingPage_controller {
    @FXML
    private ListView<String> returnTicketListView;
    @FXML
    private Button returnTicketButton;

    private ObservableList<String> userTickets = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        loadUserTickets();
    }

    private void loadUserTickets() {
        // Load user's tickets from MySQL
    }

    @FXML
    public void onReturnTicketClick() {
        String selectedTicket = returnTicketListView.getSelectionModel().getSelectedItem();

        if (selectedTicket != null) {
            String dateString = selectedTicket.split(" - Date: ")[1].split(" -")[0];
            LocalDate departureDate = LocalDate.parse(dateString);

            if (canReturnTicket(departureDate)) {
                userTickets.remove(selectedTicket);
                showAlert("Ticket Returned", "Your ticket has been successfully returned.");
            } else {
                showAlert("Return Unavailable", "Tickets cannot be returned within 2 days of departure.");
            }
        } else {
            showAlert("No Ticket Selected", "Please select a ticket to return.");
        }
    }

    private boolean canReturnTicket(LocalDate departureDate) {
        LocalDate today = LocalDate.now();
        return today.plusDays(2).isBefore(departureDate);
    }

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Ticket Booking/ViewBookingsPage.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) returnTicketListView.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Ticket Booking");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}