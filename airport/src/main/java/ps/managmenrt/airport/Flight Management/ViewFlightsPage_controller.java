package ps.managmenrt.airport;

import hostdevicedata.Flight;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class ViewFlightsPage_controller {
    @FXML
    private TableView<Map<String, String>> flightsTable;

    @FXML
    private TableColumn<Map<String, String>, String> flightIdColumn;

    @FXML
    private TableColumn<Map<String, String>, String> sourceColumn;

    @FXML
    private TableColumn<Map<String, String>, String> destinationColumn;

    @FXML
    private TableColumn<Map<String, String>, String> dateColumn;

    @FXML
    private TableColumn<Map<String, String>, String> timeColumn;

    @FXML
    private TableColumn<Map<String, String>, String> ownerColumn;

    @FXML
    private TableColumn<Map<String, String>, String> planeIdColumn;

    private ObservableList<Map<String, String>> flightsList;

    private final String DB_URL = "jdbc:mysql://localhost:3306/user_databases";
    private final String DB_USER = "root";
    private final String DB_PASS = "Root@2023";

    public void initialize() {
        // Null checks
        if (flightIdColumn != null) {
            flightIdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("flightId")));
        }
        if (sourceColumn != null) {
            sourceColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("source")));
        }
        if (destinationColumn != null) {
            destinationColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("destination")));
        }
        if (dateColumn != null) {
            dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("date")));
        }
        if (timeColumn != null) {
            timeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("time")));
        }
        if (ownerColumn != null) {
            ownerColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("owner")));
        }
        if (planeIdColumn != null) {
            planeIdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("planeId")));
        }

        loadFlightsDataFromDatabase();
    }


    private void loadFlightsDataFromDatabase() {
        flightsList = FXCollections.observableArrayList();

        String query = "SELECT flight_id, source, destination, date, time, owner, plane_id FROM flights";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Map<String, String> flight = new HashMap<>();
                flight.put("flightId", resultSet.getString("flight_id"));
                flight.put("source", resultSet.getString("source"));
                flight.put("destination", resultSet.getString("destination"));
                flight.put("date", resultSet.getString("date"));
                flight.put("time", resultSet.getString("time"));
                flight.put("owner", resultSet.getString("owner"));
                flight.put("planeId", resultSet.getString("plane_id"));

                flightsList.add(flight);
            }

            flightsTable.setItems(flightsList);

        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load flight data: " + e.getMessage());
        }
    }

    @FXML
    private void onAddFlightClick(ActionEvent event) {
        loadNewScene("Flight Management/AddFlightPage.fxml", "Add Flight");
    }

    @FXML
    private void onEditFlightClick(ActionEvent event) {
        Map<String, String> selectedFlight = flightsTable.getSelectionModel().getSelectedItem();
        if (selectedFlight != null) {        saveFlightDataToFile(selectedFlight);

            loadNewScene("Flight Management/UpdateFlightPage.fxml", "Edit Flight");
        } else {
            showAlert("Selection Error", "Please select a flight to edit.");
        }
    }

    private void saveFlightDataToFile(Map<String, String> selectedFlight) {
        try {
            File file = new File("selectedFlight.txt");  // Define the file path

            // Create a file if it doesn't exist
            if (!file.exists()) {
                file.createNewFile();
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                // Write the flight data to the file
                writer.write(selectedFlight.get("flightId") + "\n");
                writer.write(selectedFlight.get("source") + "\n");
                writer.write(selectedFlight.get("destination") + "\n");
                writer.write(selectedFlight.get("planeId") + "\n");
                writer.write(selectedFlight.get("owner") + "\n");
            }
        } catch (IOException e) {
            showAlert("File Error", "Error saving flight data to file: " + e.getMessage());
        }
    }

    @FXML
    private void onDeleteFlightClick(ActionEvent event) {
        Map<String, String> selectedFlight = flightsTable.getSelectionModel().getSelectedItem();
        if (selectedFlight != null) {
            int flightId = Integer.parseInt(selectedFlight.get("flightId"));
            Flight flight = new Flight(flightId, "", "", null, null, "", 0); // Assuming Flight has a method deleteFromDatabase()
            flight.deleteFromDatabase(); // Delete from the database
            loadFlightsDataFromDatabase(); // Refresh the table after deletion
            showAlert("Success", "Flight deleted successfully.");
        } else {
            showAlert("Selection Error", "Please select a flight to delete.");
        }
    }

    private void loadNewScene(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = flightsTable.getScene();
            Stage stage = (Stage) scene.getWindow();
            scene.setRoot(root);
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            showAlert("Navigation Error", "Failed to load the page: " + e.getMessage());
        }
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
            Stage stage = (Stage) flightsTable.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Airport Management");
            stage.show();
        } catch (IOException e) {
            showAlert("Navigation Error", "Failed to go back: " + e.getMessage());
        }
    }
}
