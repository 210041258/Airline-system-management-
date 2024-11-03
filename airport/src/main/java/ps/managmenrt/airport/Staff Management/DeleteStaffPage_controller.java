package ps.managmenrt.airport;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteStaffPage_controller {

    @FXML
    private TextField usernameField;

    private final String DB_URL = "jdbc:mysql://localhost:3306/your_database";
    private final String DB_USER = "root";
    private final String DB_PASS = "Root@2023";

    @FXML
    private void onDeleteButtonClick() {
        String username = usernameField.getText();

        if (username.isEmpty()) {
            showAlert("Input Error", "Please enter a username.");
            return;
        }

        String query = "DELETE FROM staff WHERE username = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                showAlert("Success", "Staff member deleted successfully!");
            } else {
                showAlert("Not Found", "No staff member found with that username.");
            }

        } catch (SQLException e) {
            showAlert("Database Error", "Failed to delete staff: " + e.getMessage());
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Staff Management/ViewStaffPage.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) usernameField.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Staff Management");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

