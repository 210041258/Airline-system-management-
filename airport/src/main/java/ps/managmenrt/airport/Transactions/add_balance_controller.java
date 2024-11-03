package ps.managmenrt.airport;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class add_balance_controller {

    @FXML
    private TextField usernameField;
    @FXML
    private TextField balanceField;

    @FXML
    public void onUpdateBalanceClick() {
        String username = usernameField.getText();
        String newBalanceText = balanceField.getText();

        if (username.isEmpty() || newBalanceText.isEmpty()) {
            showAlert("Input Error", "Please enter both username and balance.");
            return;
        }

        try {
            double newBalance = Double.parseDouble(newBalanceText);
            updateBalanceInDatabase(username, newBalance);
            showAlert("Success", "Balance updated for user: " + username);
        } catch (NumberFormatException e) {
            showAlert("Input Error", "Please enter a valid number for the balance.");
        }
    }

    private void updateBalanceInDatabase(String username, double newBalance) {
        // Implement database update logic here
    }
    @FXML
    public void onBackClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard/AdminDashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) usernameField.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Admin Dashboard");
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
}