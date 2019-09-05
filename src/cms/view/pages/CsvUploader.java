package cms.view.pages;

import cms.engine.helpers.FXWindowTools;
import javafx.application.Application;
import javafx.stage.Stage;

public class CsvUploader extends Application {

    @Override
    public void start(Stage stage) {
        FXWindowTools.initializeMainStage(this, stage, "/cms/view/pages/fxml/UploaderFXML/CsvUploader.fxml");
    }
}
