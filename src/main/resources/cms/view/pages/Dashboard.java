package main.resources.cms.view.pages;

import main.resources.cms.engine.helpers.FXWindowTools;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Dashboard extends Application {

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/main/resources/cms/view/pages/fxml/Dashboard.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            //stage.setResizable(false);
            stage.setScene(scene);
            stage.show();

            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();

            stage.setX(bounds.getMinX());
            stage.setY(bounds.getMinY());
            stage.setWidth(bounds.getWidth());
            stage.setHeight(bounds.getHeight());

            FXWindowTools.centerThisStage(stage);
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
}
