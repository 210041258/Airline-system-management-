package ps.managmenrt.airport;

import javafx.fxml.FXML;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.io.IOException;

public class BookTicketPage_controller {
    @FXML
    private ComboBox<String> flightComboBox;

    private ObservableList<String> availableFlights = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        loadAvailableFlights();
    }

    private void loadAvailableFlights() {
        // Load flights from MySQL
    }

    @FXML
    public void onBuyTicketClick() {
        String selectedFlight = flightComboBox.getSelectionModel().getSelectedItem();

        if (selectedFlight == null) {
            showAlert("No Flight Selected", "Please select a flight to buy a ticket.");
            return;
        }

        // Process ticket purchase for the selected flight
        showAlert("Purchase Successful", "Ticket purchased for " + selectedFlight);
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
            Stage stage = (Stage) flightComboBox.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Ticket Booking");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}