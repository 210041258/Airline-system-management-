package ps.managmenrt.airport;
import mysql.UserChecker;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import mysql.CreateusersIfNotExists;
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
            String jdbcUrl = "jdbc:mysql://localhost:3306/user_databases";
            String dbUsername = "root";
            String dbPassword = "Root@2023";

            try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword)) {

                UserChecker userChecker = new UserChecker(jdbcUrl, dbUsername, dbPassword);


                if (userChecker.checkExistingUser(username, email)) {
                    try (PreparedStatement statement = connection.prepareStatement(
                            "INSERT INTO users (username, email, password,balance) VALUES (?, ?, ?,?)")) {
                        statement.setString(1, username);
                        statement.setString(2, email);
                        statement.setString(3, password);
                        statement.setString(4, "100000");
                        int rowsAffected = statement.executeUpdate();
                        if (rowsAffected > 0) {
                            createSessionFile(username);
                            loadDashboard("Dashboard/UserDashboard.fxml", "User Dashboard");
                        } else {
                            System.err.println("Signup failed: Unexpected error inserting user.");
                        }
                    }
                } else {
                    System.err.println("Signup failed: Username or email already exists.");
                }


            } catch (SQLException e) {
                System.err.println("Signup failed: Database error: " + e.getMessage());
                mysql.CreateusersIfNotExists.main();
                e.printStackTrace(); // Replace with proper logging in a real app
            }
        } else {
            System.err.println("Signup failed: Invalid input. Please check all fields.");
        }
    }



    public void createSessionFile(String username) {
        String fileName = username + "_session.txt";
        File sessionFile = new File(fileName);

        File directory = new File(".");

        File[] files = directory.listFiles((dir, name) -> name.contains("_session.txt"));

        if (files != null) {
            for (File file : files) {
                if (file.delete()) {
                } else {
                    System.out.println("Failed to delete file: " + file.getName());
                }
            }
        }

        // Create the new session file for the provided username
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(sessionFile))) {
            writer.write("Username: " + username.trim().toLowerCase());
            writer.newLine();  // Adding a new line for better formatting
            System.out.println("New session file created: " + sessionFile.getName());
        } catch (IOException e) {
            e.printStackTrace();
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