package ps.managmenrt.airport;

import hostdevicedata.staff;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class AddStaffPage_controller {

    @FXML
    private TextField emailField;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField salaryField;

    @FXML
    private TextField jobPositionField;

    @FXML
    private void onAddButtonClick() {
        String email = emailField.getText();
        String username = usernameField.getText();
        String salaryText = salaryField.getText();
        String jobPosition = jobPositionField.getText();

        if (email.isEmpty() || username.isEmpty() || salaryText.isEmpty() || jobPosition.isEmpty()) {
            showAlert("Input Error", "Please fill in all fields.");
            return;
        }

        double salary;
        try {
            salary = Double.parseDouble(salaryText);
        } catch (NumberFormatException e) {
            showAlert("Input Error", "Salary must be a valid number.");
            return;
        }

        // Create a new staff object
        staff newStaff = new staff(username, email, salary, jobPosition);

        // Insert or update the staff record in the database
        staff.insertOrUpdateStaff(newStaff);

        showAlert("Success", "Staff member added successfully!");
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