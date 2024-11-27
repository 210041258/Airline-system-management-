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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ViewStaffPage_controller {



    @FXML
    private TableView<Map<String, String>> staffTable;

    @FXML
    private TableColumn<Map, String> emailColumn;

    @FXML
    private TableColumn<Map, String> usernameColumn;

    @FXML
    private TableColumn<Map, String> salaryColumn;

    @FXML
    private TableColumn<Map, String> jobPositionColumn;

    private ObservableList<Map<String, String>> staffList;

    private final String DB_URL = "jdbc:mysql://localhost:3306/user_databases";
    private final String DB_USER = "root";
    private final String DB_PASS = "Root@2023";

    public void initialize() {
        emailColumn.setCellValueFactory(new MapValueFactory<>("email"));
        usernameColumn.setCellValueFactory(new MapValueFactory<>("username"));
        salaryColumn.setCellValueFactory(new MapValueFactory<>("salary"));
        jobPositionColumn.setCellValueFactory(new MapValueFactory<>("jobPosition"));
        loadStaffDataFromDatabase();
    }

    private void loadStaffDataFromDatabase() {
        staffList = FXCollections.observableArrayList();
        String query = "SELECT email, username, salary, job_position FROM staff";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Map<String, String> staff = new HashMap<>();
                staff.put("email", resultSet.getString("email"));
                staff.put("username", resultSet.getString("username"));
                staff.put("salary", resultSet.getString("salary"));
                staff.put("jobPosition", resultSet.getString("job_position"));
                staffList.add(staff);
            }

            staffTable.setItems(staffList);

        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load staff data: " + e.getMessage());
        }
    }

    @FXML
    private void onAddStaffClick(ActionEvent event) {
        try {
            Button button = (Button) event.getSource();
            Scene scene = button.getParent().getScene();
            Stage stage = (Stage) scene.getWindow();
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Staff Management/AddStaffPage.fxml")));
            scene.setRoot(root);
            stage.setTitle("Add Staff");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onUpdateStaffClick(ActionEvent event) {
        try {
            Button button = (Button) event.getSource();
            Scene scene = button.getParent().getScene();
            Stage stage = (Stage) scene.getWindow();
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Staff Management/UpdateStaffPage.fxml")));
            scene.setRoot(root);
            stage.setTitle("Update Staff");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onDeleteStaffClick(ActionEvent event) {
        try {
            Button button = (Button) event.getSource();
            Scene scene = button.getParent().getScene();
            Stage stage = (Stage) scene.getWindow();
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Staff Management/DeleteStaffPage.fxml")));
            scene.setRoot(root);
            stage.setTitle("Delete Staff");
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
            Stage stage = (Stage) staffTable.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Admin Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

