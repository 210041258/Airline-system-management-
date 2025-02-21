package ps.managmenrt.airport;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.Arrays;
import java.util.Comparator;

public class ContactSupportPage_controller {

    @FXML
    private TextArea feedbackTextArea;
    @FXML
    private Button sendButton;
    @FXML
    private Button backButton;

    public static String getUsernameFromSessionFile() {
        String username = null;
        File directory = new File("."); // Current directory (consider specifying a session folder)

        // Filter for files containing "_session" in their name
        File[] sessionFiles = directory.listFiles((dir, name) -> name.toLowerCase().contains("_session"));

        if (sessionFiles == null || sessionFiles.length == 0) {
            System.out.println("No session file found.");
            return null;
        }

        // Sort session files by last modified time (latest first)
        Arrays.sort(sessionFiles, Comparator.comparingLong(File::lastModified).reversed());

        File sessionFile = sessionFiles[0]; // Pick the latest session file
        System.out.println("Reading from session file: " + sessionFile.getName());

        try (BufferedReader reader = new BufferedReader(new FileReader(sessionFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Username:")) {
                    username = line.substring("Username:".length()).trim();
                    System.out.println("Username found: " + username); // Debug log
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading session file: " + e.getMessage());
        }

        return username;
    }


    @FXML
    void handleSend(ActionEvent event) {
        String feedback = feedbackTextArea.getText();

        String jdbcUrl = "jdbc:mysql://localhost:3306/";
        String dbUsername = "root";
        String dbPassword = "Root@2023";
        String databaseName = "airlines_suggestion_issues";
        String tableName = "support_feedback";
        String username = getUsernameFromSessionFile(); // Replace with actual username


        if (!feedback.isEmpty()) {
            try (Connection serverConn = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);
                 Statement serverStmt = serverConn.createStatement()) {

                serverStmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + databaseName);


                String dbUrl = jdbcUrl + databaseName;
                try (Connection dbConn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
                     Statement stmt = dbConn.createStatement()) {

                    String createTableSQL = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY," +
                            "username VARCHAR(255)," + // New column for username
                            "feedback TEXT" +
                            ")";
                    stmt.executeUpdate(createTableSQL);


                    // 4. Insert feedback
                    try (PreparedStatement preparedStatement = dbConn.prepareStatement(
                            "INSERT INTO " + tableName + " (username, feedback) VALUES (?, ?)")) {
                        preparedStatement.setString(1, username); // Set the username value
                        preparedStatement.setString(2, feedback); // Set the feedback value
                        preparedStatement.executeUpdate();
                        System.out.println("Feedback submitted successfully!");
                        feedbackTextArea.clear();
                        handleBack(event);
                    }



                }

            } catch (SQLException e) {
                System.err.println("Error: " + e.getMessage()); // More helpful error message
                e.printStackTrace(); // Replace with logging in a real app
            }


        } else {
            System.err.println("Please enter feedback.");
        }
    }

    @FXML
    void handleBack(ActionEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard/UserDashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("User Dashboard");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}