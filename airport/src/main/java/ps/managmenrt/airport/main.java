package ps.managmenrt.airport;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.util.Objects;

public class main  {

    @FXML
    public void handleLogin(ActionEvent event) {
        try {
            Button button = (Button) event.getSource();
            Scene scene = button.getParent().getScene();
            Stage stage = (Stage) scene.getWindow();
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("User Authentication/LoginPage.fxml")));
            scene.setRoot(root);
            stage.setTitle("Login Page");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    @FXML
    public void handleSignUp(ActionEvent event) {
        try {
            Button button = (Button) event.getSource();
            Scene scene = button.getParent().getScene();
            Stage stage = (Stage) scene.getWindow();
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("User Authentication/SignupPage.fxml")));
            scene.setRoot(root);
            stage.setTitle("Sign Up Page");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

}
