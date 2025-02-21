package ps.managmenrt.airport;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import hostdevicedata.staff;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteStaffPage_controller {

    @FXML
    private TextField usernameField;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/your_database";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "Root@2023";

    /**
     * Deletes the staff member based on the entered username.
     */
    @FXML
    private void onDeleteButtonClick() {
        String username = usernameField.getText().trim();

        if (username.isEmpty()) {
            showAlert("Input Error", "Please enter a username.");
            return;
        }

        try {
            // Call the delete method from the staff class
            if(staff.deleteStaffByUsername(username)==1){ showAlert("Success", "Staff member with username '" + username + "' has been deleted.");}
else{
                showAlert("Error", "Failed to delete staff member: "+ username);

            }
        } catch (Exception e) {
            e.printStackTrace(); // Optional: For debugging
        }
    }

    /**
     * Shows an alert dialog with a given title and message.
     *
     * @param title   The title of the alert.
     * @param message The content message of the alert.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Navigates back to the Staff Management page.
     */
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
            showAlert("Navigation Error", "Failed to navigate to the staff management page.");
            e.printStackTrace();
        }
    }
}
