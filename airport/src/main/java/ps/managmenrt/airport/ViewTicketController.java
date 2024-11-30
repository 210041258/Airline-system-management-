package ps.managmenrt.airport;

import hostdevicedata.Ticket;
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
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;



public class ViewTicketController {

    private int counnt_anti=0;

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
    private final List<Ticket> tickets = new ArrayList<>(); // Store Ticket objects

    @FXML
    public void initialize() {
        ticketListView.setItems(ticketData);
        loadTickets(); // Load initial tickets from the database

        // Add a listener to handle item selection in the ListView
        ticketListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                populateFieldsFromSelectedTicket(newValue);
            }
        });
    }

    private void populateFieldsFromSelectedTicket(String selectedTicket) {
        // Assuming the format is: ID: <id>, Flight ID: <flightId>, Start Date: <startDate>, End Date: <endDate>, Price: <price>
        String[] parts = selectedTicket.split(", ");
        if (parts.length == 5) {
            // Extract ticket details from the string
            String idPart = parts[0]; // e.g., "ID: 1"
            String flightIdPart = parts[1]; // e.g., "Flight ID: FL123"
            String startDatePart = parts[2]; // e.g., "Start Date: 2023-10-01"
            String endDatePart = parts[3]; // e.g., "End Date: 2023-10-10"
            String pricePart = parts[4]; // e.g., "Price: 100"

            // Populate the fields
            ticketIdField.setText(idPart.split(": ")[1]); // Extract the ID
            flightIdField.setText(flightIdPart.split(": ")[1]); // Extract the Flight ID
            startDateField.setText(startDatePart.split(": ")[1]); // Extract the Start Date
            endDateField.setText(endDatePart.split(": ")[1]); // Extract the End Date
            priceField.setText(pricePart.split(": ")[1]); // Extract the Price
        }
    }

    private void loadTickets() {

        List<Ticket> tickets = Ticket.loadAllTickets();
        ticketData.clear();
        for (Ticket ticket : tickets) {
            ticketData.add(ticket.toString());
        }
    }

    @FXML
    private void onSearchButtonClick(ActionEvent event) {
        counnt_anti=1;
        try {
            int searchId = Integer.parseInt(ticketIdField.getText());
            Ticket ticket = Ticket.getTicketByTicketId(searchId);

            if (ticket != null) {
                String searchResult = ticket.toString(); // Convert ticket to string only if it exists
                ticketData.clear();
                ticketData.add(searchResult); // Add the ticket information to the ListView
                statusLabel.setText("Ticket found: " + searchId); // Optional: Display a success message
            } else {
                statusLabel.setText("No ticket found with ID: " + searchId);
            }
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid Ticket ID format.");
        } catch (Exception e) {
            statusLabel.setText("An error occurred: " + e.getMessage());
        }
    }


    @FXML
    private void onpriceButtonClick(ActionEvent event) {
        String price = priceField.getText();


        if (validateInputs(price)) {
            try {
                int basePrice = Integer.parseInt(price);
                if (basePrice < 0) {
                    statusLabel.setText("Please enter a valid positive price.");
                    return;
                }

                // Calculate the range dynamically based on the number of digits
                int digitLength = price.length();
                int rangeIncrement = (int) Math.pow(10, digitLength - 1) - 1;  // e.g., 100 -> 99, 1000 -> 999

                int minPrice = basePrice;
                int maxPrice = basePrice + rangeIncrement;

                List<Ticket> tickets = Ticket.getTicketsByPriceRange(minPrice, maxPrice);
                displayTickets(tickets);
                statusLabel.setText(tickets.isEmpty() ? "No tickets found in the specified price range." : "Tickets retrieved successfully.");

            } catch (NumberFormatException e) {
                statusLabel.setText("Invalid price input. Please enter a valid number.");
            } catch (Exception e) {
                statusLabel.setText("Error retrieving tickets: " + e.getMessage());
            }
        } else {
            statusLabel.setText("Please enter a valid price.");
        }

    }

    @FXML
    private void onrefreshButtonClick(ActionEvent event) {
        if(counnt_anti==1){
            counnt_anti=0;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Ticket Booking/ViewTicket.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setTitle("Ticket Management");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            statusLabel.setText("Error going back: " + e.getMessage());
        }
    }
    }

    @FXML
    private void onendstarttidButtonClick(ActionEvent event) {
        String ticketId = ticketIdField.getText();
        String startDate = startDateField.getText();
        String endDate = endDateField.getText();
    
        if (validateInputs(ticketId, startDate, endDate)) {
            try {
                List<Ticket> tickets = Ticket.getTicketsByFlightAndDateRange(Integer.parseInt(ticketId), 
                                                                             Date.valueOf(startDate), 
                                                                             Date.valueOf(endDate));
                // Display tickets in ListView or appropriate UI component
                displayTickets(tickets);
            } catch (Exception e) {
                statusLabel.setText("Error retrieving tickets: " + e.getMessage());
            }
        }
    }
    
    @FXML
    private void onoflightButtonClick(ActionEvent event) {

        String flightId = flightIdField.getText();
    
        if (validateInputs(flightId)) {
            try {
                List<Ticket> tickets = Ticket.getTicketsByFlight(Integer.parseInt(flightId));
                displayTickets(tickets);
            } catch (Exception e) {
                statusLabel.setText("Error retrieving tickets: " + e.getMessage());
            }
        }
    }
    
    @FXML
    private void onendButtonClick(ActionEvent event) {
        String endDate = endDateField.getText();
    
        if (validateInputs(endDate)) {
            try {
                List<Ticket> tickets = Ticket.getTicketsByFlightAndEndDate(Date.valueOf(endDate));
                displayTickets(tickets);
            } catch (Exception e) {
                statusLabel.setText("Error retrieving tickets: " + e.getMessage());
            }
        }
    }
    
    @FXML
    private void onstartButtonClick(ActionEvent event) {
        String startDate = startDateField.getText();
    
        if (validateInputs(startDate)) {
            try {
                List<Ticket> tickets = Ticket.getTicketsByFlightAndStartDate(Date.valueOf(startDate));
                displayTickets(tickets);
            } catch (Exception e) {
                statusLabel.setText("Error retrieving tickets: " + e.getMessage());
            }
        }
    }
    
    // Input validation method
    private boolean validateInputs(String... inputs) {
        for (String input : inputs) {
            if (input == null || input.trim().isEmpty()) {
                statusLabel.setText("Please fill in all fields.");
                return false;
            }
        }
        return true;
    }
    @FXML
    private void onAddButtonClick(ActionEvent event) {
        if(validateFields(1)){
        try {
            // Validate flight ID
            String flightID = flightIdField.getText();
            if (flightID == null || flightID.isEmpty()) {
                statusLabel.setText("Flight ID cannot be empty.");
                return;
            }

            // Validate dates
            Date startDate = Date.valueOf(startDateField.getText());
            if (startDate == null) return; // Error message already set in parseDate

            Date endDate = Date.valueOf(endDateField.getText());
            if (endDate == null) return; // Error message already set in parseDate

            // Validate price
            int price = parsePrice(priceField.getText());
            if (price == -1) return; // Error message already set in parsePrice

            // Create and save the new ticket
            Ticket newTicket = new Ticket(0,Integer.parseInt(flightID), startDate, endDate, price);
            newTicket.saveToDatabase();  // Save to database
            tickets.add(newTicket);
            ticketData.add(newTicket.toString());

            statusLabel.setText("Ticket Added Successfully.");
            clearFields();

        } catch (Exception e) {
            statusLabel.setText("An error occurred. Please try again.");
            e.printStackTrace();
        }}
    }
    // Method to display tickets in the UI
    private void displayTickets(List<Ticket> tickets) {
        counnt_anti=1;
        if (tickets.isEmpty()) {
            statusLabel.setText("No tickets found.");
        } else {
            // Assuming you have a ListView or another component to display the tickets
            ticketData.clear();
            for (Ticket ticket : tickets) {
                ticketData.add(ticket.toString()); // Make sure to override toString() in Ticket class
            }
            ticketListView.setItems(ticketData);
            statusLabel.setText(tickets.size() + " tickets found.");
        }
    }
    private LocalDate parseDate(String dateText, String fieldName) {
        try {
            return LocalDate.parse(dateText, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (java.time.format.DateTimeParseException e) {
            statusLabel.setText(fieldName + " format is incorrect. Please use yyyy-MM-dd.");
            return null;
        }
    }

    // Helper method to parse price and handle invalid price inputs
    private int parsePrice(String priceText) {
        try {
            return Integer.parseInt(priceText);
        } catch (NumberFormatException e) {
            statusLabel.setText("Price must be a valid number.");
            return -1;  // Return an invalid value to indicate an error
        }
    }
    @FXML
    private void onUpdateButtonClick(ActionEvent event) {
        if (validateFields(0)) {
            try {
                counnt_anti=1;

                int ticketId = Integer.parseInt(ticketIdField.getText());
                int flightId = Integer.parseInt(flightIdField.getText());
                Date startDate = Date.valueOf(startDateField.getText());
                Date endDate = Date.valueOf(endDateField.getText());
                double price = Double.parseDouble(priceField.getText());

                Ticket ticketToUpdate = new Ticket(ticketId, flightId, startDate, endDate, price);
                ticketToUpdate.updateInDatabase(flightId, startDate, endDate, price, ticketId); // Update in database

                statusLabel.setText("Ticket Updated Successfully");
                clearFields();
                loadTickets(); // Reload tickets to show the updated ticket
            } catch (Exception e) {
                statusLabel.setText("Error updating ticket: " + e.getMessage());
            }
        }
    }

    @FXML
    private void onDeleteButtonClick(ActionEvent event) {
        try {
            int ticketId = Integer.parseInt(ticketIdField.getText());
            Ticket ticketToDelete = Ticket.getTicketByTicketId(ticketId);
            if (ticketToDelete != null) {
                ticketToDelete.deleteFromDatabase(); // Delete from database
                statusLabel.setText("Ticket deleted successfully.");
                clearFields();
                loadTickets(); // Reload tickets to reflect deletion
            } else {
                statusLabel.setText("No ticket found with that ID.");
            }
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid input. Please enter a numeric Ticket ID.");
        }
    }

    private boolean validateFields(int a) {
        if(a==0){
        if (ticketIdField.getText().isEmpty() || flightIdField.getText().isEmpty() ||
            startDateField.getText().isEmpty() || endDateField.getText().isEmpty() ||
            priceField.getText().isEmpty()) {
            statusLabel.setText("All fields must be filled out.");
            return false;
        }

        try {
            LocalDate startDate = LocalDate.parse(startDateField.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDate endDate = LocalDate.parse(endDateField.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            if (endDate.isBefore(startDate)) {
                statusLabel.setText("End date must be after start date.");
                return false;
            }
        } catch (Exception e) {
            statusLabel.setText("Invalid date format. Use yyyy-MM-dd.");
            return false;
        }

        try {
            Double.parseDouble(priceField.getText());
        } catch (NumberFormatException e) {
            statusLabel.setText("Price must be a valid number.");
            return false;
        }

        return true;}else {

                if (flightIdField.getText().isEmpty() ||
                        startDateField.getText().isEmpty() || endDateField.getText().isEmpty() ||
                        priceField.getText().isEmpty()) {
                    statusLabel.setText("All fields must be filled out.");
                    return false;
                }

                try {
                    LocalDate startDate = LocalDate.parse(startDateField.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    LocalDate endDate = LocalDate.parse(endDateField.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    if (endDate.isBefore(startDate)) {
                        statusLabel.setText("End date must be after start date.");
                        return false;
                    }
                } catch (Exception e) {
                    statusLabel.setText("Invalid date format. Use yyyy-MM-dd.");
                    return false;
                }

                try {
                    Double.parseDouble(priceField.getText());
                } catch (NumberFormatException e) {
                    statusLabel.setText("Price must be a valid number.");
                    return false;
                }

                return true;
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
            stage.setTitle("User  Dashboard");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            statusLabel.setText("Error going back: " + e.getMessage());
        }
    }
}