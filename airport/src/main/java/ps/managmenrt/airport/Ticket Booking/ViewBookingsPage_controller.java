package ps.managmenrt.airport;

import hostdevicedata.Flight;
import hostdevicedata.Ticket;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.util.List;

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
        ticketListView.setOnMouseClicked(this::onTicketListViewClick); // Set mouse click event
    }

    private void onTicketListViewClick(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) { // Check for double-click
            String selectedTicketInfo = ticketListView.getSelectionModel().getSelectedItem();
            if (selectedTicketInfo != null) {
                // Extract ticket ID from the selected ticket info
                int ticketId = Integer.parseInt(selectedTicketInfo.split(",")[0].split(":")[1].trim());
                storeTicketIdInTempFile(ticketId);
                openBookingView();

            }
        }
    }

    private void storeTicketIdInTempFile(int ticketId) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("temp_ticket_id.txt"))) { // Specify the path for the temp file
                writer.write(String.valueOf(ticketId));
            } catch (IOException e) {
                e.printStackTrace();
        }
    }


    private void loadFlights() {
        List<Flight> flightList = Flight.loadAllFlights(); // Load flights from the Flight class
        for (Flight flight : flightList) {
            String flightInfo = String.format("Flight ID: %d, From: %s, To: %s, Date: %s, Time: %s",
                    flight.getFlightId(), flight.getSource(), flight.getDestination(), flight.getDate(), flight.getTime());
            flights.add(flightInfo); // Add formatted flight info to the combo box
        }
        flightComboBox.setItems(flights); // Set the items for the combo box
    }

    @FXML
    public void onViewTicketsButtonClick() {
        String selectedFlightInfo = flightComboBox.getSelectionModel().getSelectedItem();
        if (selectedFlightInfo == null) {
            ticketListView.setItems(FXCollections.observableArrayList("No flight selected."));
            return;
        }

        // Extract flight ID from selected flight info
        int flightId = Integer.parseInt(selectedFlightInfo.split(",")[0].split(":")[1].trim());

        // Load tickets for the selected flight
        List<Ticket> ticketList = Ticket.getTicketsByFlight(flightId);
        tickets.clear(); // Clear previous tickets
        for (Ticket ticket : ticketList) {
            tickets.add(ticket.toString()); // Add ticket info to the list
        }
        ticketListView.setItems(tickets); // Set the items for the list view
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

    @FXML
    public void gocancel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Ticket Booking/CancelBookingPage.fxml"));
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
    private void openBookingView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Ticket Booking/BookTicketPage.fxml"));
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