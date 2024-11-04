package ps.managmenrt.airport;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

    public class AdminDashboard_controller {

        @FXML
        private Button manageFlightsButton;
        @FXML
        private Button managePassengersButton;
        @FXML
        private Button manageStaffButton;
        @FXML
        private Button viewReportsButton;
        @FXML
        private Button systemSettingsButton;
        @FXML
        private AnchorPane contentArea;
        @FXML
        private Button logoutButton;


        @FXML
        void handleViewFlightsReportsButtonAction(ActionEvent event) {
            loadPage("Reports and Analytics/FlightReportsPage.fxml",event,"View Flights Reports");
        }

        @FXML
        void handleViewPassengerReportsButtonAction(ActionEvent event) {
            loadPage("Reports and Analytics/PassengerReportsPage.fxml",event,"View Passenger Reports");
        }


        @FXML
        void handleViewFlightsButtonAction(ActionEvent event) {
            loadPage("Flight Management/ViewFlightsPage.fxml",event,"View Flights");
        }

        @FXML
        void handleAddBalanceButtonAction(ActionEvent event) {
            loadPage("Transactions/add_balance.fxml",event,"Add Balance");
        }

        @FXML
        void handleViewPassengersButtonAction(ActionEvent event) {
            loadPage("Passenger Management/ViewPassengersPage.fxml",event,"View Passengers");
        }

        @FXML
        void handleviewticketButtonAction(ActionEvent event) {
            loadPage("Ticket Booking/ViewTicket.fxml",event,"View Tickets");
        }

        @FXML
        void handleViewStaffButtonAction(ActionEvent event) {
            loadPage("Staff Management/ViewStaffPage.fxml",event,"View Staff");
        }
        @FXML
        void handleLogoutButtonAction() {
            // 1. Close the current admin dashboard window (download the pdfs and save the mysql data updates and make backup )
            Stage currentStage = (Stage) logoutButton.getScene().getWindow();
            currentStage.close();


            try {
                Parent root = FXMLLoader.load(getClass().getResource("main-view.fxml"));
                Stage loginStage = new Stage();
                loginStage.setScene(new Scene(root));
                loginStage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void loadPage(String fxmlFileName, ActionEvent event,String s) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFileName));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle(s);
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
