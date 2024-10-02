package ps.managmenrt.airport;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class AddPassengerPage_controller {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}