package ps.managmenrt.airport;

import hostdevicedata.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import java.io.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;

public class CancelBookingPage_controller {

    @FXML
    private ListView<String> returnTicketListView;
    @FXML
    private Button returnTicketButton;

    private List<Ticket> userTickets;
    private int _ticketID;
    @FXML
    public void initialize() {
        loadUserTickets();
        returnTicketListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            returnTicketButton.setDisable(newValue == null);
        });
        returnTicketButton.setDisable(true);
    }

    private void loadUserTickets() {
        String username = getUsernameFromSessionFile();
        if (username == null) {
            showAlert("Error", "Unable to retrieve user session.");
            return;
        }

        List<Orders> userOrders = Orders.loadOrdersByUsername(username);
        returnTicketListView.getItems().clear(); // Clear the ListView before populating it

        for (Orders order : userOrders) {
            int ticketId = order.getTicketId();
            int orderCount = order.getOrderCount();
            Date creationDate = new Date(order.getCreationDate().getTime()); // Convert Timestamp to Date

            Ticket ticket = Ticket.getTicketByTicketId(ticketId);
            Flight flight = Flight.getFlightByTicketId(ticketId); // Fetch flight details by ticket ID

            if (ticket != null && flight != null) {
                returnTicketListView.getItems().add(
                        "Order ID: " + order.getOrderId() + "\n" +
                        "Ticket ID: " + ticket.getTicketId() + "\n" +
                        "Ticket Start Date: " + ticket.getStartDate() + "\n" +
                        "Ticket End Date: " + ticket.getEndDate() + "\n" +
                        "Ticket Source: " + flight.getSource() + "\n" +
                        "Ticket Destination: " + flight.getDestination() + "\n" +
                        "Price: " + (orderCount * ticket.getPrice()) + "\n" +
                        "Creation Date: " + creationDate + "\n"
                );
            }
        }
    }

    @FXML
    public void onReturnTicketClick() {
        String selectedTicket = returnTicketListView.getSelectionModel().getSelectedItem();

        if (selectedTicket != null) {
            try {
                // Use regex to extract the ticket ID from the selected ticket string
                String ticketIdString = selectedTicket.replaceAll("(?s).*Ticket ID: (\\d+).*", "$1");

                if (ticketIdString.isEmpty()) {
                    throw new NumberFormatException("Ticket ID could not be found.");
                }

                int ticketId = Integer.parseInt(ticketIdString); // Parse the ticket ID

                // Retrieve ticket details by ticket ID
                Ticket ticket = Ticket.getTicketByTicketId(ticketId);
                if (ticket != null) {
                    // Extract the departure date from the selected ticket string
                    String departureDateString = selectedTicket.split("Ticket End Date: ")[1].split("\n")[0];
                    LocalDate departureDate = LocalDate.parse(departureDateString); // Parse the date

                    // Check if the ticket can be returned
                    if (canReturnTicket(departureDate)) {
                        double ticketPrice = ticket.getPrice();
                        String username = getUsernameFromSessionFile();
                        passenger Passenger = passenger.searchByUsername(username); // Ensure Passenger class is defined

                        if (Passenger != null) {
                            double currentBalance = Passenger.getBalance();
                            double newBalance = currentBalance + ticketPrice;

                            // Update passenger balance and save the updated details
                            Passenger.updateUserAndPassenger(username, newBalance, Passenger.getEmail(), Passenger.getPassword());

                            // Delete the order associated with the ticket
                            Orders.deleteOrderByTicketId(ticket.getTicketId());

                            // Create a cancel transaction and save it
                            Date creationDate = new Date(System.currentTimeMillis());
                            transactions cancelTransaction = new transactions(0, username,ticketPrice, creationDate, "Cancel Ticket", String.valueOf(ticketId));
                            cancelTransaction.saveToDatabase();

                            // Remove the canceled ticket from the ListView
                            returnTicketListView.getItems().remove(selectedTicket);

                            // Show confirmation alert
                            showAlert("Ticket Canceled", "Your ticket has been successfully canceled.");
                        } else {
                            showAlert("Passenger Not Found", "No passenger found with the username: " + username);
                        }
                    } else {
                        showAlert("Cancellation Unavailable", "Tickets cannot be canceled within 2 days of departure.");
                    }
                } else {
                    showAlert("Ticket Not Found", "The selected ticket does not exist.");
                }
            } catch (NumberFormatException e) {
                showAlert("Invalid Selection", "The selected ticket ID is invalid: " + e.getMessage());
            } catch (Exception e) {
                showAlert("Error", "An unexpected error occurred: " + e.getMessage());
            }
        } else {
            showAlert("No Ticket Selected", "Please select a ticket to return.");
        }
    }

    private boolean canReturnTicket(LocalDate departureDate) {
        LocalDate today = LocalDate.now();
        return today.plusDays(2).isBefore(departureDate);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Ticket Booking/ViewBookingsPage.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) returnTicketListView.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Ticket Booking");
            stage.show();
        } catch (IOException e) {
            showAlert("Navigation Error", "Unable to navigate back to the bookings page.");
            e.printStackTrace();
        }
    }

    public static String getUsernameFromSessionFile() {
        String username = null;
        File directory = new File("."); // Current directory

        File[] sessionFiles = directory.listFiles((dir, name) -> name.toLowerCase().contains("_session"));

        if (sessionFiles == null || sessionFiles.length == 0) {
            System.out.println("No session file found.");
            return null;
        }

        File sessionFile = sessionFiles[0];
        System.out.println("Reading from session file: " + sessionFile.getName());

        try (BufferedReader reader = new BufferedReader(new FileReader(sessionFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Username:")) {
                    username = line.substring("Username:".length()).trim();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return username;
    }
}