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

public class UpdateStaffPage_controller {

    @FXML
    private TextField emailField;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField salaryField;

    @FXML
    private TextField jobPositionField;

    private final String DB_URL = "jdbc:mysql://localhost:3306/your_database";
    private final String DB_USER = "root";
    private final String DB_PASS = "Root@2023";

    private String staffEmail; // Email of the staff member to be updated

    // Method to populate fields with existing staff information
    public void setStaffDetails(String email, String username, String salary, String jobPosition) {
        this.staffEmail = email;
        emailField.setText(email);
        usernameField.setText(username);
        salaryField.setText(salary);
        jobPositionField.setText(jobPosition);
    }

    @FXML
    private void onUpdateButtonClick() {
        String email = emailField.getText();
        String username = usernameField.getText();
        String salary = salaryField.getText();
        String jobPosition = jobPositionField.getText();

        if (email.isEmpty() || username.isEmpty() || salary.isEmpty() || jobPosition.isEmpty()) {
            showAlert("Input Error", "Please fill in all fields.");
            return;
        }

        String query = "UPDATE staff SET username = ?, salary = ?, job_position = ? WHERE email = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            statement.setString(2, salary);
            statement.setString(3, jobPosition);
            statement.setString(4, staffEmail);
            statement.executeUpdate();

            showAlert("Success", "Staff member updated successfully!");

        } catch (SQLException e) {
            showAlert("Database Error", "Failed to update staff: " + e.getMessage());
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
            Stage stage = (Stage) emailField.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Staff Management");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

