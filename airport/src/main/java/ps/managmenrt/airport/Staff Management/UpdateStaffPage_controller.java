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

    private String staffEmail; // Email of the staff member to be updated

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
        String salaryStr = salaryField.getText();
        String jobPosition = jobPositionField.getText();

        if (email.isEmpty() || username.isEmpty() || salaryStr.isEmpty() || jobPosition.isEmpty()) {
            showAlert("Input Error", "Please fill in all fields.");
            return;
        }

        double salary;
        try {
            salary = Double.parseDouble(salaryStr);
        } catch (NumberFormatException e) {
            showAlert("Input Error", "Salary must be a valid number.");
            return;
        }

        // Create a staff object with the updated details
        staff updatedStaff = new staff(username, email, salary, jobPosition);

        staff.insertOrUpdateStaff(updatedStaff);
        showAlert("Success", "Staff member updated successfully!");
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