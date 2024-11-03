package ps.managmenrt.airport;

import javafx.fxml.FXML;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class report_balance_controller {
    @FXML
    private ListView<String> usernameListView;

    private ObservableList<String> usernames = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        loadUsernames();
    }

    private void loadUsernames() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/airport_management", "root", "password")) {
            PreparedStatement statement = connection.prepareStatement("SELECT username FROM users");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                usernames.add(resultSet.getString("username"));
            }
            usernameListView.setItems(usernames);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Unable to load usernames.");
        }
    }

    @FXML
    public void onViewTransactionsClick() {
        String selectedUsername = usernameListView.getSelectionModel().getSelectedItem();

        if (selectedUsername != null) {
            viewTransactionsForUser(selectedUsername);
        } else {
            showAlert("Selection Error", "Please select a username to view transactions.");
        }
    }

    private void viewTransactionsForUser(String username) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/airport_management", "root", "password")) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM transactions WHERE username = ?");
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            StringBuilder transactionDetails = new StringBuilder("Transactions for " + username + ":\n");
            while (resultSet.next()) {
                transactionDetails.append("Transaction ID: ").append(resultSet.getInt("transaction_id"))
                        .append(", Amount: ").append(resultSet.getDouble("amount"))
                        .append(", Date: ").append(resultSet.getDate("date")).append("\n");
            }
            showAlert("Transactions", transactionDetails.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Unable to retrieve transactions for " + username);
        }
    }

    @FXML
    public void onBackClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard/UserDashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) usernameListView.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("User Dashboard");
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
