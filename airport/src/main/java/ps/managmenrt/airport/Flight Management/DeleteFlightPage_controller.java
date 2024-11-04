package ps.managmenrt.airport;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;


public class DeleteFlightPage_controller {

    @FXML
    private TextField flightIdField;

    @FXML
    private Label feedbackLabel;

    @FXML
    private void onDeleteButtonClick() {
        String flightId = flightIdField.getText();
        if (isInputValid(flightId)) {
            deleteFlightData(flightId);
        } else {
            showFeedback("Please enter a Flight ID to delete.", true);
        }
    }

    private boolean isInputValid(String flightId) {
        return flightId != null && !flightId.isEmpty();
    }

    private void deleteFlightData(String flightId) {
        // Replace this with actual database delete logic
        // DatabaseConnector db = new DatabaseConnector();
        // boolean success = db.deleteFlight(flightId);

        // For example purposes, assume delete was successful
        boolean success = true; // simulate successful deletion

        if (success) {
            showFeedback("Flight ID " + flightId + " has been deleted.", false);
        } else {
            showFeedback("Error: Flight ID " + flightId + " could not be deleted.", true);
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