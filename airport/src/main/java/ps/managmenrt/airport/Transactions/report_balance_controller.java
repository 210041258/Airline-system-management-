package ps.managmenrt.airport;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class report_balance_controller{
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}