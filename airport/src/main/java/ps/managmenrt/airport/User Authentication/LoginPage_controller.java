package ps.managmenrt.airport;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;
import java.io.*;
import java.nio.file.*;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginPage_controller {

    @FXML
    private RadioButton adminRadioButton;
    @FXML
    private RadioButton userRadioButton;

    @FXML
    private VBox adminLoginBox;
    @FXML
    private VBox userLoginBox;

    @FXML
    private TextField adminUsernameField;
    @FXML
    private PasswordField adminPasswordField;

    @FXML
    private TextField userUsernameField;
    @FXML
    private PasswordField userPasswordField;

    @FXML
    public void handleRoleSelection() {

        if (adminRadioButton.isSelected()) {
            adminLoginBox.setVisible(true);
            userLoginBox.setVisible(false);
            userRadioButton.setSelected(false);
        } else if (userRadioButton.isSelected()) {
            userLoginBox.setVisible(true);
            adminLoginBox.setVisible(false);
            adminRadioButton.setSelected(false);
        }
        if(!userRadioButton.isSelected()){
            userLoginBox.setVisible(false);
        }
        if(!adminRadioButton.isSelected()){
            adminLoginBox.setVisible(false);
        }

    }

    @FXML
    public void handleAdminLogin(ActionEvent event) {
        String username = adminUsernameField.getText();
        String password = adminPasswordField.getText();

        if (username.equals("ro") && password.equals("ro")) {
            loadDashboard(event, "Dashboard/AdminDashboard.fxml", "Admin Dashboard");
        } else {
            displayErrorMessage("Invalid admin credentials.");
        }
    }

    @FXML
    public void handleUserLogin(ActionEvent event) {
        String username = userUsernameField.getText();
        String password = userPasswordField.getText();
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/user_databases", "root", "Root@2023");
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?")) {

            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                // create file named as the session data with username and export the username (just username search and assing what you will find for that on the users table )
                //
                createSessionFile(username);
                loadDashboard(event, "Dashboard/UserDashboard.fxml", "User Dashboard");
            } else {
                displayErrorMessage("Invalid user credentials.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            displayErrorMessage("Database error: " + e.getMessage());
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

    private void loadDashboard(ActionEvent event, String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            displayErrorMessage("Error loading dashboard: " + e.getMessage());
        }
    }

    private void displayErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void goToMain(ActionEvent event) throws IOException {
        try{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main-view.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("User Dashboard");
        stage.show();}
        catch (Exception e){
            e.printStackTrace();
        }
    }
}