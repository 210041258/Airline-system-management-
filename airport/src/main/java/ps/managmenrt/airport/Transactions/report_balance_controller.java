package ps.managmenrt.airport;
import java.util.Timer;
import java.util.TimerTask;
import javafx.fxml.FXML;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import hostdevicedata.transactions;  // Import the transactions class

import java.io.IOException;
import java.util.List;

public class report_balance_controller {

    @FXML
    private ListView<String> transactionListView; // ListView to display transactions

    private ObservableList<String> transactionDetails = FXCollections.observableArrayList(); // ObservableList for binding data

    // Method that runs when the controller is initialized
    @FXML
    public void initialize() {
        loadTransactions(); // Load transaction data
    }

    // Load transaction data from the database
    private void loadTransactions() {
        try {
            List<transactions> transactionsList = transactions.loadAllTransactionsFromDatabase(); // Fetch transactions
            // Populate the ListView with transaction strings
            for (transactions transaction : transactionsList) {
                String transactionString = "Username: " + transaction.getUsername() +
                        ", Amount: " + transaction.getAmount() +
                        ", Date: " + transaction.getDate() +
                        ", Type: " + transaction.getTypeTransaction() +
                        ", Message: " + transaction.getTransactionMessage();
                transactionDetails.add(transactionString); // Add to the observable list
            }

            transactionListView.setItems(transactionDetails); // Bind the data to the ListView
        }
        catch (NullPointerException e) {
                showAlert("error not existing !@!", "data flow not complete ! security issues 606 ! ");
        }
    }

    // Method to handle clicking on a transaction in the ListView
    @FXML
    public void onViewTransactionsClick() {
        String selectedTransaction = transactionListView.getSelectionModel().getSelectedItem();

        if (selectedTransaction != null) {
            showAlert("Selected Transaction", selectedTransaction); // Show an alert with selected transaction details
        } else {
            showAlert("Selection Error", "Please select a transaction to view."); // Show an error if no transaction is selected
        }
    }

    // Method to handle the 'Back' button click event
    @FXML
    public void onBackClick() {
        try {
            // Load the User Dashboard scene
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard/UserDashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) transactionListView.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("User Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace(); // Handle any I/O exceptions when loading the scene
        }
    }

    // Helper method to display alerts
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait(); // Show the alert to the user
    }
}
