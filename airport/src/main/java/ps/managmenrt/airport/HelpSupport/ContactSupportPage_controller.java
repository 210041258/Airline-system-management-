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
import java.sql.*;

public class ContactSupportPage_controller {

    @FXML
    private TextArea feedbackTextArea;
    @FXML
    private Button sendButton;
    @FXML
    private Button backButton;


    @FXML
    void handleSend(ActionEvent event) {
        String feedback = feedbackTextArea.getText();

        String jdbcUrl = "jdbc:mysql://localhost:3306/";
        String dbUsername = "root";
        String dbPassword = "Root@2023";
        String databaseName = "airlines_suggestion_issues";
        String tableName = "support_feedback";


        if (!feedback.isEmpty()) {
            try (Connection serverConn = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);
                 Statement serverStmt = serverConn.createStatement()) {

                serverStmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + databaseName);


                String dbUrl = jdbcUrl + databaseName;
                try (Connection dbConn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
                     Statement stmt = dbConn.createStatement()) {

                    String createTableSQL = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY," +
                            "feedback TEXT" +
                            ")";
                    stmt.executeUpdate(createTableSQL);


                    // 4. Insert feedback
                    try (PreparedStatement preparedStatement = dbConn.prepareStatement(
                            "INSERT INTO " + tableName + " (feedback) VALUES (?)")) {
                        preparedStatement.setString(1, feedback);
                        preparedStatement.executeUpdate();  // No need to check rowsAffected here
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