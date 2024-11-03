package ps.managmenrt.airport;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class UpdateFlightPage_controller {

    @FXML
    private TextField flightIdField;

    @FXML
    private TextField sourceField;

    @FXML
    private TextField destinationField;

    @FXML
    private TextField planeIdField;

    @FXML
    private TextField airlineField;

    @FXML
    private Label feedbackLabel;
    @FXML
    public void initialize() {
        clearFields();
        feedbackLabel.setVisible(false);
    }
    private void clearFields() {
        flightIdField.clear();
        sourceField.clear();
        destinationField.clear();
        planeIdField.clear();
        airlineField.clear();
    }
    @FXML
    private void onUpdateButtonClick() {
        String flightId = flightIdField.getText();
        String source = sourceField.getText();
        String destination = destinationField.getText();
        String planeId = planeIdField.getText();
        String airline = airlineField.getText();

        if (isInputValid(flightId, source, destination, planeId, airline)) {
            updateFlightData(flightId, source, destination, planeId, airline);
        } else {
            showFeedback("Please fill out all fields.", true);
        }
    }

    private boolean isInputValid(String... fields) {
        for (String field : fields) {
            if (field == null || field.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private void updateFlightData(String flightId, String source, String destination, String planeId, String airline) {
        // Replace with actual database update logic
        // DatabaseConnector db = new DatabaseConnector();
        // boolean success = db.updateFlight(flightId, source, destination, planeId, airline);

        // For example purposes, simulate a successful update
        boolean success = true;

        if (success) {
            showFeedback("Flight " + flightId + " has been updated successfully.", false);
        } else {
            showFeedback("Error updating flight. Please try again.", true);
        }
    }

    private void showFeedback(String message, boolean isError) {
        feedbackLabel.setText(message);
        feedbackLabel.getStyleClass().removeAll("error");
        if (isError) {
            feedbackLabel.getStyleClass().add("error");
        }
        feedbackLabel.setVisible(true);
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