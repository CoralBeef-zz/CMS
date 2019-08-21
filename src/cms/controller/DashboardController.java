package cms.controller;

import cms.engine.connection.mq.TaskMQFeeder;
import cms.engine.uploader.CSVUploader;
import cms.engine.connection.crawlserver.ServerThread;
import cms.engine.tasks.CrawlTaskManager;
import cms.engine.uploader.Uploader;
import cms.model.Arachnid;
import cms.model.Task;
import cms.model.ui.*;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import org.bson.types.ObjectId;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static cms.engine.helpers.FXWindowTools.*;


public class DashboardController {


    @FXML private VBox taskRootPane;
    @FXML private FlowPane crawlerRootPane;
    @FXML private GridPane uploaderPane;
    @FXML private ComboBox<String> crawledDataList;
    @FXML private TextField uploadCSVTextField;
    @FXML private ProgressBar uploadProgressBar;
    @FXML private ProgressIndicator uploadProgressIndicator;
    @FXML private Label uploadProgressLabel;
    @FXML private Button uploadButton;
    @FXML private Button browseCSVButton;
    @FXML private HBox uploaderStatusBar;

    private HashMap<ObjectId, ArachnidBox> arachnidBoxHashMap = new HashMap<>();
    private CsvColumnSelector csvColumnSelector;

    @FXML
    public void initialize() {
        //new CrawledDataListLoader(this).initiate();
        CrawledDataListLoader crawledDataListLoader = new CrawledDataListLoader(this);
        Thread initializerThread = new Thread(crawledDataListLoader);
        initializerThread.setDaemon(true);
        initializerThread.start();


        ServerThread serverThread = new ServerThread(this);
        Thread serverActualThread = new Thread(serverThread);
        serverActualThread.setDaemon(true);
        serverActualThread.start();

        Thread taskThread = new Thread(CrawlTaskManager.getInstance().getTaskThread());
        taskThread.setDaemon(true);
        taskThread.start();

        taskRootPane.getChildren().add(new AddTaskPane(this).onTitledPane("Add New Crawl Task"));

        uploadButton.setOnAction(e -> {
            Thread uploadThread = new Thread(new Uploader(uploadButton, uploadProgressLabel, uploadProgressBar, uploadProgressIndicator, crawledDataList.getSelectionModel().getSelectedItem()));
            uploadThread.setDaemon(true);
            uploadThread.start();
        });

        uploadCSVTextField.textProperty().addListener((obs, oldText, newText) -> {
            try {
                updateCSVHeaderSelector(newText, uploaderPane, 1, 10);
            } catch (IOException exc) {

            }
        });

        browseCSVButton.setOnAction(e -> {
            try {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Find CSV file");
                fileChooser.setInitialDirectory(
                        new File(System.getProperty("user.home"))
                );
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("CSV File", "*.csv")
                );
                String csvFile = fileChooser.showOpenDialog(browseCSVButton.getScene().getWindow()).getAbsolutePath();
                uploadCSVTextField.setText(csvFile);

                updateCSVHeaderSelector(csvFile, uploaderPane, 1, 11);
            } catch (IOException exc) {

            }
        });
    }

    public void addTaskGroup(Pane parentPane, String targetSite, String targetPartition) {
        Task newTask = new Task(new ObjectId(), 2, targetPartition, targetSite, "P01_MAIN_PAGE", "");
        parentPane.getChildren().add(new TaskGroupPane(parentPane,newTask).onTitledPane(targetSite+" on Partition: "+targetPartition));

        TaskMQFeeder taskMQFeeder = new TaskMQFeeder();
        taskMQFeeder.sendTask(newTask);
    }

    public ComboBox<String> getCrawledDataList() {
        return crawledDataList;
    }

    public VBox getTaskRootPane() {
        return taskRootPane;
    }

    public void activateCrawler(Arachnid arachnid) {
        CrawlTaskManager.getInstance().getActiveCrawlerList().add(arachnid);
        ArachnidBox newBox = new ArachnidBox();
        arachnidBoxHashMap.put(arachnid.getId(), newBox);
        newBox.setText(arachnid.getStatus());
        crawlerRootPane.getChildren().add(newBox);
    }

    public void updateCrawlerStatus(Arachnid arachnid) {
        ArrayList<Arachnid> activeCrawlerList = CrawlTaskManager.getInstance().getActiveCrawlerList();
        for(int l = 0; l < activeCrawlerList.size(); l++) {
            Arachnid currentArachnid = activeCrawlerList.get(l);
            if(currentArachnid.getId().toString().equals(arachnid.getId().toString())) {
                String currentStatus = arachnid.getStatus();
                currentArachnid.setStatus(currentStatus);
                arachnidBoxHashMap.get(arachnid.getId()).display(arachnid.getStatus());
                if(!currentStatus.equals("AVAILABLE")) arachnidBoxHashMap.get(arachnid.getId()).setInactive();
            }
        }

    }

    private void updateCSVHeaderSelector(String csvSource, GridPane pane, int col, int row) throws IOException {
        File csvfile = new File(csvSource);
        CSVUploader csvUploader = new CSVUploader(csvfile);
        ArrayList<String> csvUploadHeaders = csvUploader.getHeaders();

        Node previousColSelector = pane.lookup("#csvColumnSelector");
        if(previousColSelector != null) pane.getChildren().remove(previousColSelector);

        csvColumnSelector = new CsvColumnSelector(pane,csvUploadHeaders, uploadCSVTextField);
        pane.add(csvColumnSelector, col, row);
    }
}
