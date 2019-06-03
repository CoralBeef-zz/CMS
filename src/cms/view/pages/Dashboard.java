package cms.view.pages;

import cms.helpers.FXWindowTools;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Dashboard {
    public void start(Stage stage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/cms/view/Dashboard.fxml"));
        Scene scene =  new Scene(root, 1000 ,700);
        stage.setScene(scene);
        stage.show();

        FXWindowTools.centerThisStage(stage);
    }
}
