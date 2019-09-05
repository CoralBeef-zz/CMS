package cms.controller.v2controllers.UploaderControllers;

import cms.view.pages.CsvUploader;
import cms.view.pages.MainUploader;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import static cms.engine.helpers.FXWindowTools.*;

public class UploaderController {
    @FXML private FlowPane rootPane;

    @FXML
    public void initialize() {
        rootPane.getStylesheets().add(this.getClass().getResource(
                "/cms/view/pages/stylesheets/ImagesCSS.css").toExternalForm());

        //Button mainUploaderButton = generateButton("masterUploader", "Master Uploader");
        VBox mainUploaderButton = new DashboardButton("Master Uploader", "Uploads from Local DB to Master DB");
        VBox csvUploaderButton = new DashboardButton("CSV Uploader", "Uploads from CSV File to Local DB");
        VBox insertUploaderButton = new DashboardButton("Column Insert", "Inserts new columns to Existing DB");

        mainUploaderButton.setOnMouseClicked(e ->
            openPage((Stage) mainUploaderButton.getScene().getWindow(), new MainUploader())
        );

        csvUploaderButton.setOnMouseClicked(e ->
            openPage((Stage) csvUploaderButton.getScene().getWindow(), new CsvUploader())
        );

        rootPane.getChildren().addAll(mainUploaderButton, csvUploaderButton, insertUploaderButton);
    }

    private class DashboardButton extends VBox {
        public DashboardButton(String title, String desc) {
            super();
            setPrefSize(400, 700);
            getStyleClass().add("dashboardButton");
            setAlignment(Pos.BASELINE_CENTER);

            Label titleLabel = new Label(title);
            titleLabel.getStyleClass().add("dashboardButton-title");

            Label descLabel = new Label(desc);
            descLabel.getStyleClass().add("dashboardButton-desc");

            getChildren().addAll(titleLabel, descLabel);

        }
    }
}
