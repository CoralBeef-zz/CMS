package main.resources.cms.controller.v2controllers.UploaderControllers;

import main.resources.cms.engine.connection.crawlserver.ConnectionManager;
import main.resources.cms.engine.helpers.FXWindowTools;
import main.resources.cms.engine.uploader.CSVUploader;
import main.resources.cms.model.Columns;
import main.resources.cms.view.pages.Dashboard;
import com.google.common.collect.Lists;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.bson.Document;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import static main.resources.cms.engine.helpers.FXWindowTools.openPage;

public class CsvUploaderController {
    @FXML private Button leftPanelAdd;
    @FXML private Button leftPanelRemove;
    @FXML private Button leftPanelMatch;
    @FXML private ListView leftPanelList;

    @FXML private Button rightPanelMatch;
    @FXML private ListView rightPanelList;

    @FXML private Button backButton;

    @FXML private ComboBox finalCollectionName;
    private ObservableList<String> finalCollectionNameList = FXCollections.observableArrayList();
    @FXML private Button finalUploadButton;

    @FXML private VBox builder;

    private TextField matchDbColumn;
    private TextField matchCsvColumn;
    private VBox insertColumnPane = new InsertColumnPane();
    @FXML private VBox insertColumnPaneParent;

    private ObservableList<String> leftPanelChoices = FXCollections.observableArrayList();
    private ObservableList<String> rightPanelChoices = FXCollections.observableArrayList();

    private static String SPLITTER = " <> ";

    @FXML public void initialize() {
        loadLeftPanel();
        loadRightPanel();

        leftPanelMatch.setOnAction(e -> {
            String selected_text = leftPanelList.getSelectionModel().getSelectedItem().toString();
            matchCsvColumn.setText(selected_text.split(SPLITTER)[0]);
        });
        rightPanelMatch.setOnAction(e -> {
            String selected_text = rightPanelList.getSelectionModel().getSelectedItem().toString();
            matchDbColumn.setText(selected_text.split(SPLITTER)[1]);
        });

        leftPanelList.setOnMouseClicked(click -> {
            if (click.getClickCount() >= 2) {
                String currentItemSelected = (String) leftPanelList.getSelectionModel().getSelectedItem();
                generateNewField(currentItemSelected);
                leftPanelChoices.remove(currentItemSelected);
            }
        });
        rightPanelList.setOnMouseClicked(click -> {
            if (click.getClickCount() >= 2) {
                String currentItemSelected = (String) rightPanelList.getSelectionModel().getSelectedItem();

                if (currentItemSelected.split(SPLITTER)[1].equals(Columns.NEWGRADUATESITE.val)) {
                    //Corner Case
                    if(builder.getChildren().size() > 0) {
                        this.builder.getChildren().add(new BuilderBox(Columns.NEWGRADUATESITE, currentItemSelected));
                        rightPanelChoices.remove(currentItemSelected);
                    }
                } else {
                    ObservableList<Node> children = builder.getChildren();

                    for (Node child : children) {
                        if (child instanceof BuilderBox) {
                            BuilderBox toPut = (BuilderBox) child;
                            if (toPut.isActive) {
                                String previousText = toPut.rightField.getText();
                                toPut.rightField.setText(currentItemSelected);
                                rightPanelChoices.remove(currentItemSelected);

                                if (!previousText.equals("")) {
                                    rightPanelChoices.add(previousText);
                                    reorder(rightPanelChoices);
                                    rightPanelList.getSelectionModel().select(previousText);
                                }
                            }
                        }
                    }
                }

            }
        });


        backButton.setOnAction(e ->
            openPage((Stage) backButton.getScene().getWindow(), new Dashboard())
        );

        loadFinalCollectionFieldDropdown();
        finalCollectionName.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            checkFinalCollectionFieldChange(newValue);
        });
        finalUploadButton.setDisable(true);
        finalUploadButton.setOnAction(e -> finalCSVUpload());
    }

    private void checkFinalCollectionFieldChange(String value) {
        Iterator<String> it = this.finalCollectionNameList.iterator();
        insertColumnPaneParent.getChildren().remove(insertColumnPane);

        while(it.hasNext()) {
            String next = it.next();
            if(value.equals(next)) {
                if(!insertColumnPaneParent.getChildren().contains(this.insertColumnPane)) {
                    this.insertColumnPane = new InsertColumnPane();
                    insertColumnPaneParent.getChildren().add(1, insertColumnPane);
                }
            }
        }
    }

    private class InsertColumnPane extends VBox {
        public InsertColumnPane() {
            super();
            setAlignment(Pos.TOP_CENTER);
            setPrefHeight(80);
            setId("insertColumnPane");

            HBox hb = new HBox();
            hb.setSpacing(10);
            hb.setAlignment(Pos.BASELINE_CENTER);

            TextField matchCsvColumnX = new TextField();
            matchCsvColumnX.setId("matchCsvColumn");
            matchCsvColumnX.setPrefWidth(200);
            TextField matchDbColumnX = new TextField();
            matchDbColumnX.setId("matchDbColumn");
            matchDbColumnX.setPrefWidth(200);

            hb.getChildren().addAll(new Label("Combine Data if "), matchCsvColumnX, new Label("is equal to "), matchDbColumnX);

            getChildren().addAll(new Label("Collection already exists!"), hb);

            matchCsvColumnX.setOnMouseClicked(click -> matchCsvColumnX.setText(""));
            matchDbColumnX.setOnMouseClicked(click -> matchDbColumnX.setText(""));
            matchCsvColumn = matchCsvColumnX;
            matchDbColumn = matchDbColumnX;
        }
    }

    @SuppressWarnings("Duplicates") private void loadFinalCollectionFieldDropdown() {
        this.finalCollectionName.setItems(finalCollectionNameList);
        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() {
                ConnectionManager collection_manager = new ConnectionManager();
                MongoDatabase database = collection_manager.UbuntuDB("dataselect");

                MongoIterable<String> collectionNames = database.listCollectionNames();
                ArrayList<String> collectionList = Lists.newArrayList(collectionNames);

                int progressCount = 0;
                for (String collectionName : collectionList) {

                    updateProgress(progressCount,collectionList.size());
                    progressCount++;

                    Platform.runLater(() -> finalCollectionNameList.add(collectionName));

                    FXWindowTools.timedDelay(100);
                }
                updateProgress(progressCount,progressCount);
                return null;
            }
        };
        Thread dropdownLoadThread = new Thread(task);
        dropdownLoadThread.setDaemon(true);
        dropdownLoadThread.start();
    }

    private void finalCSVUpload() {
        ObservableList<Node> dataList = builder.getChildren();
        try {
            CSVUploader.upload(dataList, this.finalCollectionName.getSelectionModel().getSelectedItem().toString(), 1, ((BuilderBox)builder.getChildren().get(0)).csvFile);
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }
    private void generateNewField(String sel) {
        BuilderBox newBuilderBox = new BuilderBox(sel);
        this.builder.getChildren().add(newBuilderBox);
    }

    public class BuilderBox extends HBox {
        private Label leftSource;
        private TextField leftField;
        private TextField rightField;

        private ComboBox<String> leftChoices;

        private ObservableList<String> panelChoices;
        private ListView<String> panelList;

        public File csvFile;
        public boolean isActive = false;

        @SuppressWarnings("Duplicates") public BuilderBox(Columns col, String selectedItem) {
            super();

            setPrefHeight(50);
            setAlignment(Pos.CENTER);
            setPadding(new Insets(5, 20, 5, 20));
            setSpacing(10.0);
            getStyleClass().add("builderRow");

            this.panelChoices = rightPanelChoices;
            this.panelList = rightPanelList;
            getChildren().add(closeButton(selectedItem));

            String sourceColumn = col.val;

            leftChoices = new ComboBox<>();
            leftChoices.getItems().addAll("OK", "NOT_OK");
            leftChoices.setPrefWidth(200);
            leftChoices.getSelectionModel().selectFirst();
            getChildren().add(leftChoices);

            Label spacer1 = new Label(SPLITTER + sourceColumn + " AS ");
            spacer1.setPrefWidth(150);
            getChildren().add(spacer1);
            leftField = new TextField();
            leftField.prefWidth(200);
            leftField.setText(sourceColumn);
            getChildren().add(leftField);


            Label spacer2 = new Label(" = ");
            spacer2.setPrefWidth(50);
            getChildren().add(spacer2);

            rightField = new TextField();
            rightField.setEditable(false);
            rightField.setPrefWidth(200);
            rightField.setText(selectedItem);
            getChildren().add(rightField);

        }

        @SuppressWarnings("Duplicates") public BuilderBox(String selectedItem) {
            super();

            setPrefHeight(50);
            setAlignment(Pos.CENTER);
            setPadding(new Insets(5, 20, 5, 20));
            setSpacing(10.0);
            getStyleClass().add("builderRow");

            this.panelChoices = leftPanelChoices;
            this.panelList = leftPanelList;
            getChildren().add(closeButton(selectedItem));

            String sourceColumn = selectedItem.split(SPLITTER)[0];
            this.csvFile = new File(selectedItem.split(SPLITTER)[1]);
            String sourceString = this.csvFile.getName();

            leftSource = new Label(sourceString);
            leftSource.setPrefWidth(200);
            getChildren().add(leftSource);
            Label spacer1 = new Label(SPLITTER + sourceColumn + " AS ");
            spacer1.setPrefWidth(150);
            getChildren().add(spacer1);
            leftField = new TextField();
            leftField.prefWidth(200);
            leftField.setText(sourceColumn);
            getChildren().add(leftField);

            Label spacer2 = new Label(" = ");
            spacer2.setPrefWidth(50);
            getChildren().add(spacer2);

            rightField = new TextField();
            rightField.setEditable(false);
            rightField.setPrefWidth(200);
            getChildren().add(rightField);
            rightField.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, Event::consume);
            rightField.setOnMouseClicked(e -> {
                if(e.getClickCount() >= 2 ) {
                    if(!rightField.getText().equals("")) {
                        rightPanelChoices.add(rightField.getText());
                        rightPanelList.getSelectionModel().select(rightField.getText());
                    }
                    reorder(rightPanelChoices);
                    rightField.setText("");
                    deactivate();
                } else {
                    activationTrigger();
                }
            });
            setOnMouseClicked(e -> activationTrigger());

        }

        private Button closeButton(String selectedItem) {
            Button closeButton = new Button("X");
            closeButton.setOnAction(e ->  closeThis(selectedItem) );
            closeButton.setPrefSize(45, 45);
            closeButton.setPrefSize(20, 20);
            closeButton.getStyleClass().removeAll();
            closeButton.getStyleClass().add("closeButton");
            return closeButton;
        }

        public void closeThis(String selectedItem) {
            panelChoices.add(selectedItem);
            reorder(rightPanelChoices);
            panelList.getSelectionModel().select(selectedItem);
            builder.getChildren().remove(this);
        }

        private void activationTrigger() {
            ObservableList<Node> children = builder.getChildren();
            for(Node child : children) {
                if(child instanceof BuilderBox) {
                    BuilderBox toTest = (BuilderBox)child;
                    toTest.deactivate();
                }
            }
            this.activate();
        }

        public String dataString() {
            if(leftChoices != null) return this.leftChoices.getSelectionModel().getSelectedItem();
            else return this.leftField.getText();
        }

        public String columnName() {
            return this.rightField.getText().split(SPLITTER)[1];
        }

        public void activate() {
            this.isActive = true;
            this.rightField.getStyleClass().add("activatedField");
        }

        public void deactivate() {
            this.isActive = false;
            this.rightField.getStyleClass().remove("activatedField");
        }
    }

    private static void reorder(ObservableList list) {
        Platform.runLater(() -> {
            Comparator<String> comparator = Comparator.naturalOrder();

            list.sort(comparator);
        });
    }

    @SuppressWarnings("Duplicates") private void loadLeftPanel() {
        leftPanelList.setItems(leftPanelChoices);
        leftPanelAdd.setOnAction(e -> {
            try {
                loadCsvHeaderData();
            } catch (Exception exc) { exc.printStackTrace(); }
        });
        leftPanelRemove.setOnAction(e -> {
            finalUploadButton.setDisable(true);
            leftPanelRemove.setDisable(true);
            leftPanelMatch.setDisable(true);
            rightPanelMatch.setDisable(true);
            leftPanelAdd.setDisable(false);

            leftPanelChoices.clear();
            leftPanelList.getItems().clear();
            builder.getChildren().clear();
            rightPanelChoices.clear();
            rightPanelList.getItems().clear();
            loadRightPanel();
        });
    }

    private void loadRightPanel() {
        rightPanelList.setItems(rightPanelChoices);
        loadLocalData();
    }

    @SuppressWarnings("Duplicates") private void loadLocalData() {
        FXWindowTools.ProgressForm pForm = new FXWindowTools.ProgressForm();

        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() {
            ConnectionManager collection_to_get_manager = new ConnectionManager();
            MongoCollection<Document> collection_to_get = collection_to_get_manager.AWSDB("DataSelectDB")
                    .getCollection("columns");

            MongoCursor<Document> collectionList = collection_to_get.find().noCursorTimeout(true).iterator();

            long TOTAL = collection_to_get.countDocuments();
            long progressCount = 0;


            while(collectionList.hasNext()) {
                Document download_info = collectionList.next();

                updateProgress(progressCount,TOTAL);
                progressCount++;

                String columnName = download_info.getString("column");
                Integer columnGroup = download_info.getInteger("bigCategory");
                Platform.runLater(() -> rightPanelChoices.add(Columns.convertIndexToCategory(columnGroup)+SPLITTER+columnName));

                FXWindowTools.timedDelay(100);
            }
            updateProgress(progressCount,progressCount);
            reorder(rightPanelChoices);
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

    @SuppressWarnings("Duplicates") private void loadCsvHeaderData() {
        FXWindowTools.ProgressForm pForm = new FXWindowTools.ProgressForm();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Find CSV file");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV File", "*.csv"));
        String csvFileString = fileChooser.showOpenDialog(leftPanelAdd.getScene().getWindow()).getAbsolutePath();

        if (csvFileString != null && !csvFileString.equals("")) {
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() {
                    try {
                        File csvFile = new File(csvFileString);
                        CSVUploader csvUploader = new CSVUploader(csvFile);

                        ArrayList<String> csvUploadHeaders = csvUploader.getHeaders();

                        long progress = 0;
                        for (String csvHeader : csvUploadHeaders) {
                            Platform.runLater(() -> leftPanelChoices.add(csvHeader+SPLITTER+csvFile.getAbsolutePath()));
                            updateProgress(progress, csvUploadHeaders.size());
                            progress++;
                        }
                        reorder(leftPanelChoices);
                        return null;

                    } catch(IOException exc) {
                        exc.printStackTrace();
                        failed();
                        return null;
                    }
                }
            };

            pForm.activateProgressBar(task);
            task.setOnSucceeded(event -> {
                pForm.getDialogStage().close();


                finalUploadButton.setDisable(false);
                leftPanelMatch.setDisable(false);
                rightPanelMatch.setDisable(false);
                leftPanelRemove.setDisable(false);
                leftPanelAdd.setDisable(true);
            });

            pForm.getDialogStage().show();

            Thread thread = new Thread(task);
            thread.start();
        }
    }
}
