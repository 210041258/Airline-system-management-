package ps.managmenrt.airport;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    private List<String> passengers = new ArrayList<>();

    @FXML
    public void initialize() {
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

        // Check if the username or email already exists
        boolean usernameExists = passengers.stream().anyMatch(p -> p.contains("User: " + username));
        boolean emailExists = passengers.stream().anyMatch(p -> p.contains("Email: " + email));

        if (usernameExists || emailExists) {
            showAlert("Add Failed", "A passenger with this username or email already exists.");
            return;
        }

        // Confirm before adding
        Optional<ButtonType> confirmation = showConfirmationDialog("Confirm Add", "Are you sure you want to add this passenger?");
        if (confirmation.isPresent() && confirmation.get() == ButtonType.OK) {
            String passengerInfo = "User: " + username + ", Email: " + email + ", Password: " + password + ", Balance: " + balance;
            passengers.add(passengerInfo);
            updatePassengerList();
            clearFields();
        }
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

        boolean found = false;
        for (int i = 0; i < passengers.size(); i++) {
            if (passengers.get(i).contains("User: " + username)) {
                // Confirm before updating
                Optional<ButtonType> confirmation = showConfirmationDialog("Confirm Update", "Passenger found. Do you want to update this passenger's details?");
                if (confirmation.isPresent() && confirmation.get() == ButtonType.OK) {
                    passengers.set(i, "User: " + username + ", Email: " + email + ", Password: " + password + ", Balance: " + balance);
                    found = true;
                    break;
                }
            }
        }

        if (!found) {
            Optional<ButtonType> addConfirmation = showConfirmationDialog("Add New Passenger", "No passenger found with this username. Do you want to add this as a new passenger?");
            if (addConfirmation.isPresent() && addConfirmation.get() == ButtonType.OK) {
                onAddButtonClick();
            }
        }

        updatePassengerList();
        clearFields();
    }

    @FXML
    public void onDeleteButtonClick() {
        String username = usernameField.getText();

        if (username.isEmpty()) {
            showAlert("Missing Username", "Please enter a username to delete.");
            return;
        }

        boolean removed = passengers.removeIf(passenger -> passenger.contains("User: " + username));

        if (!removed) {
            showAlert("Delete Failed", "No passenger found with username: " + username);
        } else {
            showAlert("Delete Success", "Passenger with username " + username + " has been deleted.");
        }

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

        boolean found = passengers.stream()
                .anyMatch(passenger -> passenger.trim().toLowerCase().startsWith("user: " + username.toLowerCase()));

        if (found) {
            showAlert("Search Result", "Passenger found: " + username);
        } else {
            showAlert("Search Result", "No passenger found with username: " + username);
        }
    }


    private void updatePassengerList() {
        passengerListView.getItems().clear();
        passengerListView.getItems().addAll(passengers);
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

    private Optional<ButtonType> showConfirmationDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait();
    }

    private void populateFieldsWithSelectedPassenger(String passengerInfo) {
        String[] details = passengerInfo.split(", ");
        for (String detail : details) {
            if (detail.startsWith("User: ")) {
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
