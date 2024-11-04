package ps.managmenrt.airport;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class ViewTicketController {

    @FXML
    private TextField ticketIdField;
    @FXML
    private TextField flightIdField;
    @FXML
    private TextField startDateField;
    @FXML
    private TextField endDateField;
    @FXML
    private TextField priceField;
    @FXML
    private ListView<String> ticketListView;
    @FXML
    private Label statusLabel;
    @FXML
    private Button backButton;

    private final ObservableList<String> ticketData = FXCollections.observableArrayList();
    private int nextTicketId = 1; // To simulate auto-incrementing IDs
    private final List<Ticket> tickets = new ArrayList<>(); // Store Ticket objects

    private static class Ticket {  // Inner class to represent a ticket
        int id;
        String flightId;
        LocalDate startDate;
        LocalDate endDate;
        int price;

        public Ticket(int id, String flightId, LocalDate startDate, LocalDate endDate, int price) {
            this.id = id;
            this.flightId = flightId;
            this.startDate = startDate;
            this.endDate = endDate;
            this.price = price;
        }

        @Override
        public String toString() { // For displaying in ListView
            return String.format("ID: %d, Flight ID: %s, Start Date: %s, End Date: %s, Price: %d",
                    id, flightId, startDate, endDate, price);
        }
    }

    @FXML
    public void initialize() {
        ticketListView.setItems(ticketData);
    }



    @FXML
    private void onSearchButtonClick(ActionEvent event) {
        try {
            int searchId = Integer.parseInt(ticketIdField.getText());
            String searchResult = tickets.stream()
                    .filter(ticket -> ticket.id == searchId)
                    .map(Ticket::toString)
                    .findFirst()
                    .orElse("No ticket found with ID: " + searchId);

            ticketData.clear();
            ticketData.add(searchResult);
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid Ticket ID format.");
        }
    }



    @FXML
    private void onAddButtonClick(ActionEvent event) {
        try {
            String flightID = flightIdField.getText();
            LocalDate startDate = LocalDate.parse(startDateField.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDate endDate = LocalDate.parse(endDateField.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            int price = Integer.parseInt(priceField.getText());

            Ticket newTicket = new Ticket(nextTicketId++, flightID, startDate, endDate, price);
            tickets.add(newTicket);
            ticketData.add(newTicket.toString());


            statusLabel.setText("Ticket Added (Locally)");
            clearFields();
        } catch (NumberFormatException | java.time.format.DateTimeParseException e) {
            statusLabel.setText("Invalid input format. Please check dates and price.");
        }
    }


    @FXML
    private void onUpdateButtonClick(ActionEvent event) {

        try {
            int ticketId = Integer.parseInt(ticketIdField.getText());
            String flightId = flightIdField.getText();
            LocalDate startDate = LocalDate.parse(startDateField.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDate endDate = LocalDate.parse(endDateField.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            int price = Integer.parseInt(priceField.getText());



            for (int i = 0; i < tickets.size(); i++) {
                if (tickets.get(i).id == ticketId) {

                    tickets.set(i, new Ticket(ticketId, flightId, startDate, endDate, price));
                    ticketData.set(i, tickets.get(i).toString()); // Update the ObservableList
                    statusLabel.setText("Ticket Updated Successfully");
                    clearFields();
                    return; // Exit after updating
                }
            }


            statusLabel.setText("No ticket found with that ID");


        } catch (NumberFormatException | java.time.format.DateTimeParseException e) {
            statusLabel.setText("Invalid Input");
        }



    }



    @FXML
    private void onDeleteButtonClick(ActionEvent event) {
        try {
            int ticketId = Integer.parseInt(ticketIdField.getText());

            for (int i = 0; i < tickets.size(); i++) {
                if (tickets.get(i).id == ticketId) {
                    tickets.remove(i);
                    ticketData.remove(i); // Remove from ObservableList as well
                    statusLabel.setText("Ticket deleted successfully.");
                    clearFields();
                    return;
                }
            }


            statusLabel.setText("No ticket found with that ID.");
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid input. Please enter a numeric Ticket ID.");
        }
    }




    private void clearFields() {
        ticketIdField.clear();
        flightIdField.clear();
        startDateField.clear();
        endDateField.clear();
        priceField.clear();
    }

    @FXML
    private void onBackButtonClick(ActionEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard/AdminDashboard.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setTitle("User Dashboard");  // Set title for the UserDashboard
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            statusLabel.setText("Error going back: " + e.getMessage());
        }

    }
}