package cms.view.pages;

import cms.engine.helpers.FXWindowTools;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Dashboard extends Application {

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/cms/view/pages/fxml/Dashboard.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();

            FXWindowTools.centerThisStage(stage);
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
}
