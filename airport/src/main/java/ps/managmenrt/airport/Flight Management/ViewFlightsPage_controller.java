package ps.managmenrt.airport;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ViewFlightsPage_controller {
    @FXML
    private TableView<Map<String, String>> flightsTable;

    @FXML
    private TableColumn<Map, String> flightIdColumn;

    @FXML
    private TableColumn<Map, String> sourceColumn;

    @FXML
    private TableColumn<Map, String> destinationColumn;

    @FXML
    private TableColumn<Map, String> planeIdColumn;

    @FXML
    private TableColumn<Map, String> airlineColumn;

    private ObservableList<Map<String, String>> flightsList;

    private final String DB_URL = "jdbc:mysql://localhost:3306/user_dashboard";
    private final String DB_USER = "root";
    private final String DB_PASS = "Root@2023";

    public void initialize() {
        // Configure columns
        flightIdColumn.setCellValueFactory(new MapValueFactory<>("flightId"));
        sourceColumn.setCellValueFactory(new MapValueFactory<>("source"));
        destinationColumn.setCellValueFactory(new MapValueFactory<>("destination"));
        planeIdColumn.setCellValueFactory(new MapValueFactory<>("planeId"));
        airlineColumn.setCellValueFactory(new MapValueFactory<>("airline"));

        // Load data from database
        loadFlightsDataFromDatabase();
    }

    private void loadFlightsDataFromDatabase() {
        flightsList = FXCollections.observableArrayList();

        String query = "SELECT flight_id, source, destination, plane_id, airline FROM flights";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Map<String, String> flight = new HashMap<>();
                flight.put("flightId", resultSet.getString("flight_id"));
                flight.put("source", resultSet.getString("source"));
                flight.put("destination", resultSet.getString("destination"));
                flight.put("planeId", resultSet.getString("plane_id"));
                flight.put("airline", resultSet.getString("airline"));

                flightsList.add(flight);
            }

            flightsTable.setItems(flightsList);

        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load flight data: " + e.getMessage());
        }
    }

    @FXML
    private void onAddFlightClick(ActionEvent event) {
        try {
            Button button = (Button) event.getSource();
            Scene scene = button.getParent().getScene();
            Stage stage = (Stage) scene.getWindow();
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Flight Management/AddFlightPage.fxml")));
            scene.setRoot(root);
            stage.setTitle("Add Flight");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        }

    @FXML
    private void onEditFlightClick(ActionEvent event) {
        try {
            Button button = (Button) event.getSource();
            Scene scene = button.getParent().getScene();
            Stage stage = (Stage) scene.getWindow();
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Flight Management/UpdateFlightPage.fxml")));
            scene.setRoot(root);
            stage.setTitle("Edit Flight");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onDeleteFlightClick(ActionEvent event) {
        try {
            Button button = (Button) event.getSource();
            Scene scene = button.getParent().getScene();
            Stage stage = (Stage) scene.getWindow();
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Flight Management/DeleteFlightPage.fxml")));
            scene.setRoot(root);
            stage.setTitle("Delete Flight");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }

}