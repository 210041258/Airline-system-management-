package ps.managmenrt.airport;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import javafx.scene.layout.VBox;

import javafx.geometry.Insets;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HelpCenterPage_controller {

    @FXML
    private ListView<String> articleListView;

    public void initialize() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/airlines_articles", "root", "Root@2023");
             Statement statement = connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery("SELECT title FROM articles");

            List<String> articleTitles = new ArrayList<>();
            while (resultSet.next()) {
                articleTitles.add(resultSet.getString("title"));
            }

            articleListView.getItems().addAll(articleTitles);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleArticleClick(MouseEvent event) {
        if (event.getClickCount() == 2) {
            String selectedArticle = articleListView.getSelectionModel().getSelectedItem();
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/airlines_articles", "root", "Root@2023");
                 PreparedStatement statement = connection.prepareStatement("SELECT content FROM articles WHERE title = ?")) {
                statement.setString(1, selectedArticle);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    String articleContent = resultSet.getString("content");
                    showArticleDialog(selectedArticle, articleContent);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void showArticleDialog(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Article Content");
        alert.setHeaderText(title);

        // Create a label for the article content
        Label contentLabel = new Label(content);
        contentLabel.setWrapText(true);

        // Create a VBox to hold the content
        VBox contentVBox = new VBox(10, contentLabel);
        contentVBox.setPadding(new Insets(20, 20, 20, 20));

        alert.getDialogPane().setContent(contentVBox);
        alert.showAndWait();
    }


    @FXML
    public void handleBack(javafx.event.ActionEvent event) {

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