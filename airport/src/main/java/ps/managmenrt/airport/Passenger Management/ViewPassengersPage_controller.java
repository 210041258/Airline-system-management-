package ps.managmenrt.airport;

import hostdevicedata.passenger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class ViewPassengersPage_controller {

    @FXML
    private TextField usernameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField balanceField;

    @FXML
    private ListView<String> passengerListView;

    @FXML
    public void initialize() {
        updatePassengerList();
        passengerListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                populateFieldsWithSelectedPassenger(newValue);
            }
        });
    }

    @FXML
    public void onAddButtonClick() {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String balance = balanceField.getText();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || balance.isEmpty()) {
            showAlert("Incomplete Fields", "Please fill in all fields.");
            return;
        }

        // Create a new passenger object and insert it into the DB
        passenger newPassenger = new passenger(username, password, email, Double.parseDouble(balance));
        newPassenger.insertOrUpdatepassengerToDB(newPassenger);
        updatePassengerList();
        clearFields();
    }

    @FXML
    public void onUpdateButtonClick() {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String balance = balanceField.getText();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || balance.isEmpty()) {
            showAlert("Incomplete Fields", "Please fill in all fields.");
            return;
        }

        // Search for existing passenger by username
        passenger existingPassenger = passenger.searchByUsername(username);
        if (existingPassenger != null) {
            // Update passenger details
            existingPassenger.setEmail(email);
            existingPassenger.setPassword(password);
            existingPassenger.setBalance(Double.parseDouble(balance));
            existingPassenger.insertOrUpdatepassengerToDB(existingPassenger); // Save updated passenger to DB
            updatePassengerList();
            clearFields();
        } else {
            showAlert("Update Failed", "No passenger found with this username.");
        }
    }

    @FXML
    public void onDeleteButtonClick() {
        String username = usernameField.getText();

        if (username.isEmpty()) {
            showAlert("Missing Username", "Please enter a username to delete.");
            return;
        }

        // Delete passenger by username
        passenger.deletepassengerByUsername(username);
        updatePassengerList();
        clearFields();
    }

    @FXML
    public void onSearchButtonClick() {
        String username = usernameField.getText().trim();

        if (username.isEmpty()) {
            showAlert("Search Failed", "Please enter a username for searching.");
            return;
        }

        // Search for passenger by username
        passenger foundPassenger = passenger.searchByUsername(username);
        if (foundPassenger != null) {
            showAlert("Search Result", "Passenger found: " + foundPassenger.getUsername());
            populateFieldsWithSelectedPassenger(":User " + foundPassenger.getUsername().trim() +
                    ", Email: " + foundPassenger.getEmail() +
                    ", Password: " + foundPassenger.getPassword() +
                    ", Balance: " + foundPassenger.getBalance());
        } else {
            showAlert("Search Result", "No passenger found with username: " + username);
        }
    }

    private void updatePassengerList() {
        passengerListView.getItems().clear();
        // Get all passengers from the DB
        List<passenger> allPassengers = passenger.getAllpassengers();

        // Extract usernames or any other relevant information
        for (passenger p : allPassengers) {
            passengerListView.getItems().add(p.getUsername()); // Assuming you want to show the username
        }
    }

    private void clearFields() {
        usernameField.clear();
        emailField.clear();
        passwordField.clear();
        balanceField.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void populateFieldsWithSelectedPassenger(String passengerInfo) {
        String[] details = passengerInfo.split(", ");
        for (String detail : details) {
            if (detail.startsWith(":User  ")) {
                usernameField.setText(detail.substring(6));
            } else if (detail.startsWith("Email: ")) {
                emailField.setText(detail.substring(7));
            } else if (detail.startsWith("Password: ")) {
                passwordField.setText(detail.substring(10));
            } else if (detail.startsWith("Balance: ")) {
                balanceField.setText(detail.substring(9));
            }
        }
    }

    @FXML
    public void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard/AdminDashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) emailField.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Admin Dashboard");
            stage.show();
        } catch (IOException e) {
            showAlert("Navigation Error", "Failed to load the main page.");
            e.printStackTrace();
        }
    }
}
