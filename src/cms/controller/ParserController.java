package cms.controller;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.util.Duration;


public class ParserController {

    @FXML
    private ImageView dataSelectLogo;

    @FXML
    private TextField username;

    @FXML
    private TextField password;

    @FXML
    private Button submit;

    @FXML
    private Label status;

    public ParserController() {}

    @FXML
    private void initialize()
    {
        fadeIn(dataSelectLogo);
        fadeIn(username);
        fadeIn(username);
        fadeIn(password);
        fadeIn(submit);

        submit.setOnAction(event -> login());
    }

    private void login() {

    }

    private void fadeIn(Node node) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(2000), node);
        tt.setFromX(-20);
        tt.setToX(0);
        tt.play();

        FadeTransition ft = new FadeTransition(Duration.millis(2000), node);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();
    }
}
