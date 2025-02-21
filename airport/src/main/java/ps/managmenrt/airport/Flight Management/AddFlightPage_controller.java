package ps.managmenrt.airport;

import hostdevicedata.Flight;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.io.IOException;
import java.time.LocalTime;

public class AddFlightPage_controller {


        @FXML
        private TextField flightIdField;
        @FXML
        private TextField destinationField;
        @FXML
        private TextField sourceField;
        @FXML
        private TextField planeIdField;
        @FXML
        private TextField planeCompanyField;

        @FXML
        private void onInsertButtonClick() {
            if (isInputValid()) {
                insertFlightData();
            } else {
                showAlert("Error", "Please fill in all fields.");
            }
        }

        private boolean isInputValid() {
            return !flightIdField.getText().isEmpty() &&
                    !destinationField.getText().isEmpty() &&
                    !sourceField.getText().isEmpty() &&
                    !planeIdField.getText().isEmpty() &&
                    !planeCompanyField.getText().isEmpty();
        }
        

        private void insertFlightData() {
            // Get the input values from the text fields
            String flightIdStr = flightIdField.getText();
            String destination = destinationField.getText();
            String source = sourceField.getText();
            String planeIdStr = planeIdField.getText();
            String planeCompany = planeCompanyField.getText();
        
            // Validate input
            if (flightIdStr.isEmpty() || destination.isEmpty() || source.isEmpty() || planeIdStr.isEmpty() || planeCompany.isEmpty()) {
                showAlert("Error", "Please fill in all fields.");
                return;
            }
        
            try {
                int flightId = Integer.parseInt(flightIdStr); // Convert flightId to int
                int planeId = Integer.parseInt(planeIdStr); // Convert planeId to int
                String owner = planeCompany; // Assuming planeCompany is the owner name
        
                // Get today's date and current time
                Date todayDate = Date.valueOf(LocalDate.now()); // Get today's date
                Time currentTime = Time.valueOf(LocalTime.now()); // Get current time
        
                // Create a new Flight object and save it to the database
                Flight newFlight = new Flight(flightId, source, destination, todayDate, currentTime, owner, planeId); // Set date to today and time to now
                newFlight.saveToDatabase();
        
                showAlert("Success", "Flight details inserted successfully.");
            } catch (NumberFormatException e) {
                showAlert("Error", "Flight ID and Plane ID must be valid integers.");
            } catch (Exception e) {
                showAlert("Error", "An unexpected error occurred: " + e.getMessage());
            }
        }
       
        private void showAlert(String title, String content) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setContentText(content);
            alert.showAndWait();
        }

    @FXML
    public void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Flight Management/ViewFlightsPage.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) flightIdField.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Flights Management");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    }


