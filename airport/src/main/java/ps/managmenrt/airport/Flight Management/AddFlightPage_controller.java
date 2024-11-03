package ps.managmenrt.airport;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

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
            String flightId = flightIdField.getText();
            String destination = destinationField.getText();
            String source = sourceField.getText();
            String planeId = planeIdField.getText();
            String planeCompany = planeCompanyField.getText();

            // Example code for inserting into the database (modify per your database setup)
            // DatabaseConnector db = new DatabaseConnector();
            // db.insertFlight(flightId, destination, source, planeId, planeCompany);

            showAlert("Success", "Flight details inserted successfully.");
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


