package ps.managmenrt.airport.HelpSupport;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ArticleContentController {

    @FXML
    private Label articleTitleLabel;
    @FXML
    private Label articleContentLabel;

    public void setTitleAndContent(String title, String content) {
        articleTitleLabel.setText(title);
        articleContentLabel.setText(content);
    }
}
