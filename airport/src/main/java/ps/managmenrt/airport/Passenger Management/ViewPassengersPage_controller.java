package ps.managmenrt.airport;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ViewPassengersPage_controller {

    @FXML
    private TextField usernameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField balanceField;
    @FXML
    private TextField searchField;
    @FXML
    private ListView<String> passengerListView;

    private List<String> passengers = new ArrayList<>();

    @FXML
    public void onAddButtonClick() {
        String username = usernameField.getText();
        String email = emailField.getText();
        String balance = balanceField.getText();

        if (username.isEmpty() || email.isEmpty() || balance.isEmpty()) {
            showAlert("Incomplete Fields", "Please fill in all fields.");
            return;
        }

        String passengerInfo = "User: " + username + ", Email: " + email + ", Balance: " + balance;
        passengers.add(passengerInfo);
        updatePassengerList();
        clearFields();
    }

    @FXML
    public void onUpdateButtonClick() {
        String username = usernameField.getText();
        String email = emailField.getText();
        String balance = balanceField.getText();

        if (username.isEmpty() || email.isEmpty() || balance.isEmpty()) {
            showAlert("Incomplete Fields", "Please fill in all fields.");
            return;
        }

        boolean found = false;
        for (int i = 0; i < passengers.size(); i++) {
            if (passengers.get(i).contains("User: " + username)) {
                passengers.set(i, "User: " + username + ", Email: " + email + ", Balance: " + balance);
                found = true;
                break;
            }
        }

        if (!found) {
            showAlert("Passenger Not Found", "No passenger found with username: " + username);
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
            showAlert("Passenger Not Found", "No passenger found with username: " + username);
        }

        updatePassengerList();
        clearFields();
    }

    @FXML
    public void onSearchButtonClick() {
        String username = searchField.getText().trim();

        passengerListView.getItems().clear();

        if (username.isEmpty()) {
            updatePassengerList();  // Display all if search is empty
            showAlert("Username is Nothing", "Username given is null (Empty textfiled)!!  ");
            return;
        }

        boolean found = false;
        for (String passenger : passengers) {
            if (passenger.toLowerCase().contains("user: " + username.toLowerCase())) {
                passengerListView.getItems().add(passenger);
                found = true;
            }
        }

        if (!found) {
            showAlert("No Results", "No passengers found matching: " + username);
        }
    }

    private void updatePassengerList() {
        passengerListView.getItems().clear();
        passengerListView.getItems().addAll(passengers);
    }

    private void clearFields() {
        usernameField.clear();
        emailField.clear();
        balanceField.clear();
        searchField.clear();
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
