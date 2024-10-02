package ps.managmenrt.airport;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class ViewPassengersPage_controller{

        @FXML
        private ListView<String> passengersListView;
        @FXML
        private TextField passengerNameField;
        @FXML
        private TextField passportNumberField;

        // Sample data to populate the ListView for demonstration purposes
        private String[] passengers = {
                "John Doe, P123456",
                "Jane Smith, P654321",
                "Sam Wilson, P987654"
        };

        @FXML
        public void initialize() {
            // Populate the passengers list view
            passengersListView.getItems().addAll(passengers);
        }

        @FXML
        public void handlePassengerSelection() {
            // Get the selected passenger
            String selectedPassenger = passengersListView.getSelectionModel().getSelectedItem();
            if (selectedPassenger != null) {
                // Split the selected item to get name and passport number
                String[] details = selectedPassenger.split(", ");
                passengerNameField.setText(details[0]); // Set name
                passportNumberField.setText(details[1]); // Set passport number
            }
        }

        @FXML
        public void handleUpdatePassenger() {
            // Logic to update the selected passenger
            String updatedName = passengerNameField.getText();
            String updatedPassport = passportNumberField.getText();
            // Implement update logic here
            System.out.println("Updating passenger: " + updatedName + " with passport: " + updatedPassport);
        }

        @FXML
        public void handleDeletePassenger() {
            // Logic to delete the selected passenger
            String selectedPassenger = passengersListView.getSelectionModel().getSelectedItem();
            if (selectedPassenger != null) {
                passengersListView.getItems().remove(selectedPassenger);
                passengerNameField.clear();
                passportNumberField.clear();
                // Implement delete logic here
                System.out.println("Deleted passenger: " + selectedPassenger);
            }
        }
    }

