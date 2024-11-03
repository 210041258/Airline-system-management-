package ps.managmenrt.airport;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewBookingsPage_controller {

    @FXML
    private ComboBox<String> flightComboBox;
    @FXML
    private Button viewTicketsButton;
    @FXML
    private ListView<String> ticketListView;

    private ObservableList<String> flights = FXCollections.observableArrayList();
    private ObservableList<String> tickets = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        loadFlights();
    }

    private void loadFlights() {
        // Load flights from MySQL
    }

    @FXML
    public void onViewTicketsButtonClick() {
        // Load tickets for selected flight
    }

    @FXML
    public void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard/UserDashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) viewTicketsButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Flights Management");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
