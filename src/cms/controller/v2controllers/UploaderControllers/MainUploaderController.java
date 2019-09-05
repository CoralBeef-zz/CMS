package cms.controller.v2controllers.UploaderControllers;

import cms.engine.connection.crawlserver.ConnectionManager;
import cms.engine.helpers.FXWindowTools;
import cms.engine.uploader.Uploader;
import cms.view.pages.Dashboard;
import com.google.common.collect.Lists;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.bson.Document;

import java.util.ArrayList;

import static cms.engine.helpers.FXWindowTools.openPage;

public class MainUploaderController {

    @FXML private Button backButton;
    @FXML private FlowPane localDataList;

    @FXML
    public void initialize() {

        backButton.setOnAction(e ->
                openPage((Stage) backButton.getScene().getWindow(), new Dashboard())
        );

        loadLocalData(this.localDataList);
    }

    private void loadLocalData(FlowPane localDataList) {
        FXWindowTools.ProgressForm pForm = new FXWindowTools.ProgressForm();

        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() {
                ConnectionManager collection_manager = new ConnectionManager();
                MongoDatabase database = collection_manager.UbuntuDB("dataselect");

                MongoIterable<String> collectionNames = database.listCollectionNames();
                ArrayList<String> collectionList = Lists.newArrayList(collectionNames);

                int progressCount = 0;
                for (String collectionName : collectionList) {

                    MongoCollection docs = database.getCollection(collectionName);
                    Long amount = docs.countDocuments();

                    updateProgress(progressCount,collectionList.size());
                    progressCount++;

                    LocalData localData = new LocalData(collectionName,
                            ((Document) docs.find().first()).get("siteGroup").toString(),
                            amount);

                    Platform.runLater(() -> localDataList.getChildren().add(localData));

                    FXWindowTools.timedDelay(100);
                }
                updateProgress(progressCount,progressCount);
                return null;
            }
        };

        pForm.activateProgressBar(task);
        task.setOnSucceeded(event -> {
            pForm.getDialogStage().close();
        });

        pForm.getDialogStage().show();

        Thread thread = new Thread(task);
        thread.start();
    }

    private class LocalData extends GridPane {
        public String TARGET_COLLECTION;

        private final ProgressBar pb = new ProgressBar();
        private final ProgressIndicator pin = new ProgressIndicator();
        private final Label pl = new Label();

        private Task uploaderTask;

        public LocalData(String collectionName, String siteGroup, Long dataAmount) {
            super();
            this.TARGET_COLLECTION = collectionName;
            setAlignment(Pos.BASELINE_CENTER);
            setPrefSize(650, 100);
            setPadding(new Insets(20, 20, 20, 20));
            getStyleClass().add("dataListBox");

            Label collectionNameLabel = new Label(collectionName);
            collectionNameLabel.setPrefSize(200, 50);
            collectionNameLabel.getStyleClass().add("h2");
            add(collectionNameLabel, 0, 0, 2, 1);

            Pane spacer = new Pane();
            spacer.setPrefWidth(50);
            add(spacer, 0, 1);

            Label siteGroupLabel = new Label("Site Group: ");
            siteGroupLabel.setPrefSize(200, 30);
            add(siteGroupLabel, 1, 3);
            Label siteGroupDisplay = new Label(siteGroup+"");
            siteGroupDisplay.setPrefSize(100, 30);
            add(siteGroupDisplay, 2, 3);

            Label dataAmountLabel = new Label("Amount of Data: ");
            dataAmountLabel.setPrefSize(200, 30);
            add(dataAmountLabel, 1, 5);
            Label dataAmountDisplay = new Label(dataAmount+"");
            dataAmountDisplay.setPrefSize(100, 30);
            add(dataAmountDisplay, 2, 5);

            Button uploadButton = new Button("Upload");
            uploadButton.getStyleClass().add("button2");
            uploadButton.setPrefSize(100, 40);
            add(uploadButton, 3, 3);

            Pane spacer3 = new Pane();
            spacer3.setMinHeight(5);
            add(spacer3, 0, 4);

            Button cancelButton = new Button("Cancel");
            cancelButton.getStyleClass().add("button2");
            cancelButton.setPrefSize(100, 40);
            cancelButton.setDisable(true);
            add(cancelButton, 3, 5);

            Pane spacer2 = new Pane();
            spacer2.setMinHeight(20);
            add(spacer2, 0, 6);

            pb.setProgress(-1F);
            pb.setMinSize(400, 20);
            pb.setPrefSize(400, 20);

            pin.setProgress(-1F);
            pin.setMinSize(100, 50);
            pin.setPrefSize(100, 50);

            pl.setMinSize(100, 20);
            pl.setPrefSize(100, 20);

            this.uploaderTask = new Uploader(pl, collectionName);
            this.uploaderTask.setOnSucceeded(e -> {
                deactivateProgressBar(uploadButton, cancelButton);
            });
            this.uploaderTask.setOnCancelled(e -> {
                deactivateProgressBar(uploadButton, cancelButton);
            });

            uploadButton.setOnAction(e -> {
                this.uploaderTask = new Uploader(pl, collectionName);

                activateProgressBar(this.uploaderTask);
                uploadButton.setDisable(true);
                cancelButton.setDisable(false);

                Thread uploaderTaskThread = new Thread(this.uploaderTask);
                uploaderTaskThread.setDaemon(true);
                uploaderTaskThread.start();
            });

            cancelButton.setOnAction(e -> {
                this.uploaderTask.cancel();
                deactivateProgressBar(uploadButton, cancelButton);
            });
        }

        public void deactivateProgressBar(Button uploadButton, Button cancelButton) {
            uploadButton.setDisable(false);
            cancelButton.setDisable(true);
            getChildren().removeAll(pb, pin, pl);
        }

        public void activateProgressBar(final Task<?> task)  {
            add(pb, 0, 7, 3, 1);
            add(pin, 3, 7);
            add(pl, 4, 7);

            pb.progressProperty().bind(task.progressProperty());
            pin.progressProperty().bind(task.progressProperty());
        }
    }


}
