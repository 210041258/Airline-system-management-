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

        if (!feedback.isEmpty()) {
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/airlines_suggestion_issues", "root", "Root@2023");
                 Statement statement = connection.createStatement()) {

                // Check if the table exists
                ResultSet resultSet = statement.executeQuery("SELECT 1 FROM support_feedback LIMIT 1");
                if (resultSet.next()) {
                    // Table exists, proceed with insertion
                    try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO support_feedback (feedback) VALUES (?)")) {
                        preparedStatement.setString(1, feedback);
                        int rowsAffected = preparedStatement.executeUpdate();
                        if (rowsAffected > 0) {
                            // Feedback successfully inserted
                            feedbackTextArea.clear();
                            handleBack(event);

                            System.out.println("Feedback submitted successfully!");
                        } else {
                            // Error inserting feedback
                            System.err.println("Error submitting feedback.");
                        }
                    }
                } else {
                    // Table does not exist, create it first
                    // Then, try to insert again
                    try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO support_feedback (feedback) VALUES (?)")) {
                        preparedStatement.setString(1, feedback);
                        int rowsAffected = preparedStatement.executeUpdate();
                        if (rowsAffected > 0) {
                            // Feedback successfully inserted
                            feedbackTextArea.clear();
                            handleBack(event);
                            System.out.println("Feedback submitted successfully!");
                        } else {
                            // Error inserting feedback
                            System.err.println("Error submitting feedback.");
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            // Feedback is empty, display an error message
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