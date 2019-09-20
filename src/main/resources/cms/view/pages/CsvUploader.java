package main.resources.cms.view.pages;

import main.resources.cms.engine.helpers.FXWindowTools;
import javafx.application.Application;
import javafx.stage.Stage;

public class CsvUploader extends Application {

    @Override
    public void start(Stage stage) {
        FXWindowTools.initializeMainStage(this, stage, "/main/resources/cms/view/pages/fxml/UploaderFXML/CsvUploader.fxml");
    }
}
