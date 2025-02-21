package ps.managmenrt.airport;

import hostdevicedata.Orders;
import hostdevicedata.Ticket;
import hostdevicedata.Orders;
import hostdevicedata.passenger;
import hostdevicedata.transactions;
import javafx.fxml.FXML;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.*;
import java.sql.Date;
import java.util.Arrays;
import java.util.Comparator;

public class BookTicketPage_controller {
    @FXML
    private ComboBox<String> flightComboBox;

    @FXML
    private Label staus;

    private ObservableList<String> availabletickets = FXCollections.observableArrayList();


    @FXML
    public void initialize() {
        loadAvailableTickets();
    }

    private void loadAvailableTickets() {
        File tempFile = new File("temp_ticket_id.txt");
        if (tempFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(tempFile))) {
                String line = reader.readLine(); // Read the first line
                if (line != null) {
                    try {
                        int ticketId = Integer.parseInt(line.trim()); // Parse as an integer
                        loadTicketsById(ticketId);
                    } catch (NumberFormatException e) {
                        goBack();
                    }
                } else {
                    goBack();
                }
            } catch (IOException e) {
                showAlert("File Error", "An error occurred while reading the file: " + e.getMessage());
            } finally {
                if (tempFile.delete()) {
                    System.out.println("Temporary file deleted successfully.");
                } else {
                    System.out.println("Failed to delete the temporary file.");
                }
            }
        } else {
            goBack();
        }
    }

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


    public int ticketId  ;
    private void  loadTicketsById(int ticketId_tyer) {
        ticketId =ticketId_tyer;
        Ticket ticket = Ticket.getTicketByTicketId(ticketId_tyer);
        if (ticket != null) {
            flightComboBox.getItems().clear();
            flightComboBox.getItems().add(ticket.toString());
            flightComboBox.getSelectionModel().selectFirst();
                showAlert("Ticket Existed !! ", "Ticket ID " + ticketId_tyer + " You can buy it !.");
        } else {
            showAlert("Ticket Not Found", "No ticket found with ID " + ticketId_tyer);
        }
    }


    @FXML
    public void onBuyTicketClick() {
        String selectedFlight = flightComboBox.getSelectionModel().getSelectedItem();
        if (selectedFlight == null) {
            showAlert("No Flight Selected", "Please select a flight to buy a ticket.");
            return;
        }
    
        Ticket ticket = Ticket.getTicketByTicketId(ticketId);
        double ticketPrice = ticket.getPrice();
    
        String username = getUsernameFromSessionFile();
        double currentBalance = passenger.getBalanceByUsername(username); // Method to get user balance
    
        if (currentBalance < ticketPrice) {
            showAlert("Insufficient Balance", "You do not have enough balance to purchase this ticket.");
            return;
        }
    
        // Update user's balance
        double newBalance = currentBalance - ticketPrice;
        passenger p1 = passenger.searchByUsername(username);
        p1.updateUserAndPassenger(p1.getUsername(),newBalance,p1.getEmail(),p1.getPassword());
        Date creationDate = new Date(System.currentTimeMillis());
        transactions newTransaction = new transactions(0, username, ticketPrice, creationDate, "Buying Ticket", String.valueOf(ticketId));
        newTransaction.saveToDatabase(); // Ensure to save the transaction to the database
    
        showAlert("Purchase Successful", "Successfully purchased for " + selectedFlight);
        staus.setText("You can cancel the ticket before the end date of it 72 hrs");
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
            Stage stage = (Stage) flightComboBox.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Ticket Booking");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}