package cms.view.old.pages;

import cms.engine.helpers.FXWindowTools;
import cms.controller.CrawlerStatusController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Dashboard extends Application {

    private CrawlerStatusController crawlerStatusController;

    @Override
    public void start(Stage stage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/cms/view/old/Dashboard.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene =  new Scene(root, 700 ,800);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

        crawlerStatusController = fxmlLoader.getController();
        FXWindowTools.centerThisStage(stage);
    }

    @Override
    public void stop(){
        System.out.println("Stage is closing");
    }

}
