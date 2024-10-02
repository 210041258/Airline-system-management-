package ps.managmenrt.airport;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.sql.*;

public class SignupPage_controller {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    public void handleSignup() {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (validateInput(username, email, password, confirmPassword)) {
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/users", "root", "Root@2023");
                 PreparedStatement statement = connection.prepareStatement("INSERT INTO users (username, email, password) VALUES (?, ?, ?)")) {

                // Assume you have a table named 'users' with columns 'username', 'email', 'password'
                statement.setString(1, username);
                statement.setString(2, email);
                statement.setString(3, password);

                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("User signed up successfully: " + username);
                    // Navigate to User Dashboard after successful signup
                    loadDashboard("Dashboard/UserDashboard.fxml", "User Dashboard");
                } else {
                    System.out.println("Signup failed: Error inserting user into the database.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Signup failed: Database error.");
            }
        } else {
            System.out.println("Signup failed: Please check your input.");
        }
    }

    private boolean validateInput(String username, String email, String password, String confirmPassword) {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            return false;
        }

        if (!password.equals(confirmPassword)) {
            return false;
        }

        return true;
    }

    @FXML
    public void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("main-view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) usernameField.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Airport Management System");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadDashboard(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}