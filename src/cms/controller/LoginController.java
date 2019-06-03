package cms.controller;

import cms.helpers.FXWindowTools;
import cms.view.pages.Dashboard;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class LoginController {

    @FXML
    private VBox loginRoot;

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

    public LoginController() {}

    @FXML
    private void initialize()
    {
        FXWindowTools.fadeInNode(dataSelectLogo);
        FXWindowTools.fadeInNode(username);
        FXWindowTools.fadeInNode(username);
        FXWindowTools.fadeInNode(password);
        FXWindowTools.fadeInNode(submit);

        submit.setDefaultButton(true);
        submit.setOnAction(event -> login());
    }

    private void login() {
        try {
            Stage stage = (Stage) submit.getScene().getWindow();
            new Dashboard().start(stage);
        } catch (Exception exc) {
            status.setText("Error Found! "+exc.toString()+" Details: "+exc.getStackTrace()[1]);
        }
    }


}
