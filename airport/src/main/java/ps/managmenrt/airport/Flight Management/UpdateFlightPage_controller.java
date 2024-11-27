package ps.managmenrt.airport;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

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

    private final String DB_URL = "jdbc:mysql://localhost:3306/user_databases";
    private final String DB_USER = "root";
    private final String DB_PASS = "Root@2023";

    @FXML
    public void initialize() {
        clearFields();
        feedbackLabel.setVisible(false);
        loadFlightDataFromFile();

    }

    private void loadFlightDataFromFile() {
        try {
            File file = new File("selectedFlight.txt");
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String flightId = reader.readLine();
                String source = reader.readLine();
                String destination = reader.readLine();
                String planeId = reader.readLine();
                String airline = reader.readLine();

                // Set the flight data to the form fields
                setFlightData(flightId, source, destination, planeId, airline);

                reader.close();
            } else {
                showAlert("File Error", "Selected flight data file not found.");
            }
        } catch (IOException e) {
            showAlert("File Error", "Error loading flight data from file: " + e.getMessage());
        }
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
        String query = "UPDATE flights SET source = ?, destination = ?, plane_id = ?, owner = ? WHERE flight_id = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, source);
            statement.setString(2, destination);
            statement.setString(3, planeId);
            statement.setString(4, airline);
            statement.setString(5, flightId);

            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                showFeedback("Flight " + flightId + " has been updated successfully.", false);
            } else {
                showFeedback("Error updating flight. Please try again.", true);
            }

        } catch (SQLException e) {
            showFeedback("Error updating flight. Please try again.", true);
            e.printStackTrace();
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

    // Set the flight data in the fields when editing
    public void setFlightData(String flightId, String source, String destination, String planeId, String airline) {
        flightIdField.setText(flightId);
        sourceField.setText(source);
        destinationField.setText(destination);
        planeIdField.setText(planeId);
        airlineField.setText(airline);
    }
}
