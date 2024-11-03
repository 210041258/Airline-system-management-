package ps.managmenrt.airport;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.control.Button;


public class UserDashboard_controller {

        @FXML
        private Button viewProfileButton;
        @FXML
        private Button viewNotificationsButton;
        @FXML
        private Button manageSettingsButton;
        @FXML
        private Button viewReportsButton;
        @FXML
        private Button logoutButton;
        @FXML
        private Button backToMainButton;


    @FXML
    void handleViewTickets(ActionEvent event) {
        loadPage("Ticket Booking/ViewBookingsPage.fxml", event, "View Tickets");
    }

    @FXML
    void transacttions(ActionEvent event) {
        loadPage("Transactions/report_balance.fxml", event, "View Transacttions");
    }

    @FXML
    void handleHelpCenter(ActionEvent event) {
        loadPage("HelpSupport/HelpCenterPage.fxml", event, "Help Center");
    }

    @FXML
    void handleContactSupport(ActionEvent event) {
        loadPage("HelpSupport/ContactSupportPage.fxml", event, "Contact Support");
    }

    @FXML
    void handleViewReports(ActionEvent event) {
        loadPage("Reports and Analytics/ReportUserPage.fxml", event, "View Reports");
    }

    @FXML
    void handleLogout(ActionEvent event) {
        Stage currentStage = (Stage) logoutButton.getScene().getWindow();
        currentStage.close();

        loadPage("Logout Page/LogoutPage.fxml", event, "Login");
    }


    private void loadPage(String fxmlFileName, ActionEvent event, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFileName));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}