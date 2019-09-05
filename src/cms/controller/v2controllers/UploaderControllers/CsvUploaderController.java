package cms.controller.v2controllers.UploaderControllers;

import cms.engine.uploader.CSVUploader;
import cms.model.Columns;
import cms.view.pages.CsvUploader;
import cms.view.pages.Dashboard;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.bson.Document;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static cms.engine.helpers.FXWindowTools.openPage;

public class CsvUploaderController {

    @FXML private BorderPane csvUploaderRoot;

    @FXML private VBox columnSelectorRoot;
    @FXML private Button addCSVButton;
    @FXML private TextField addCSVTextField;
    @FXML private Button browseCSVButton;

    @FXML private TextField collectionNameTextField;
    @FXML private Button uploadCSVButton;
    @FXML private Button backButton;

    @FXML private VBox draggableColumns;

    private final ArrayList<ColumnSelector> columnSelectorList = new ArrayList<>();

    private int counter = 1;
    private static final String IGNORE_STRING = "IGNORE";

    @FXML @SuppressWarnings("Duplicates")
    public void initialize() {
        columnSelectorRoot.setPrefWidth(1600.0);

        browseCSVButton.setOnAction(e -> {
            try {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Find CSV file");
                fileChooser.setInitialDirectory(
                        new File(System.getProperty("user.home")));
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("CSV File", "*.csv"));
                String csvFile = fileChooser.showOpenDialog(browseCSVButton.getScene().getWindow()).getAbsolutePath();
                addCSVTextField.setText(csvFile);
            } catch (Exception exc) {  }

        });

        addCSVButton.setOnAction(e -> {
            try {
                ColumnSelector columnSelector = new ColumnSelector(columnSelectorRoot, "CSV #"+counter, addCSVTextField.getText());
                columnSelectorList.add(columnSelector);
                columnSelectorRoot.getChildren().add(columnSelector);
                this.counter++;
            } catch (IOException exc) {
                exc.printStackTrace();
            }
        });

        uploadCSVButton.setOnAction(e -> {
            HashMap<String, String> tempMap = new HashMap<>();
            ArrayList<File> addedFiles = new ArrayList<>();

            columnSelectorList.forEach((columnSelector) -> {
                tempMap.putAll(columnSelector.userEntryMap());
                addedFiles.add(columnSelector.csvFile);
            });
            try {
                CSVUploader csvUploader = new CSVUploader(collectionNameTextField.getText(), 1, tempMap, addedFiles);

                csvUploader.upload();
            } catch (IOException exc) {
                exc.printStackTrace();
            }
        });

        backButton.setOnAction(e ->
            openPage((Stage) backButton.getScene().getWindow(), new Dashboard())
        );

        fillDraggableColumns(this.draggableColumns, Columns.getColumnsFromDatabase());
    }

    private void fillDraggableColumns(VBox pane, ArrayList<Document> documents) {
        int colorCount = 1;
        for(Document document : documents) {
            pane.getChildren().add(new draggableColumnBox(document, colorCount));
            colorCount = colorCount == 1 ? 2 : 1;
        }

    }

    private class draggableColumnBox extends GridPane {
        private double startDragX;
        private double startDragY;

        public draggableColumnBox(Document document, int colorCount) {
            super();

            String colName = document.getString("column");
            Label label = new Label(colName);
            setPrefWidth(300);
            setAlignment(Pos.BASELINE_CENTER);
            getStyleClass().add("draggableColumnBox");
            getStyleClass().add("draggableColumnBox-background"+colorCount);
            add(label, 0,0);

            /*setOnMousePressed(e -> {
                startDragX = e.getSceneX();
                startDragY = e.getSceneY();
            });

            setOnMouseDragged(e -> {
                setTranslateX(e.getSceneX() - startDragX);
                setTranslateY(e.getSceneY() - startDragY);
            });*/

            setOnDragDetected(e -> {
                Dragboard dragboard = startDragAndDrop(TransferMode.MOVE);
                ClipboardContent clipboard = new ClipboardContent();
                clipboard.put(DataFormat.PLAIN_TEXT, document.toJson());

                dragboard.setContent(clipboard);
                e.consume();
            });
        }
    }

    private class ColumnSelector extends BorderPane {

        private ArrayList<TextField> entryList = new ArrayList<>();
        private ArrayList<ComboBox<String>> columnSelectionList = new ArrayList<>();
        private File csvFile;


        public ColumnSelector(Pane root, String title, String source) throws IOException {
            this.csvFile = new File(source);
            CSVUploader csvUploader = new CSVUploader(csvFile);
            ArrayList<String> csvUploadHeaders = csvUploader.getHeaders();


            Label titleLabel = new Label(title);
            Button closeButton = new Button("X");
            HBox titlePane = new HBox();
            titlePane.getChildren().addAll(titleLabel, closeButton);

            setTop(titlePane);

            FlowPane columnPane = new FlowPane();
            ObservableList<String> defaultColumnDataSet = FXCollections.observableArrayList(csvUploader.getHeaders());
            defaultColumnDataSet.add(0, IGNORE_STRING);

            csvUploadHeaders.forEach((csvHeader) -> {
                HBox entry = new HBox();
                entry.setSpacing(10);
                entry.setAlignment(Pos.CENTER);

                entry.setMinSize(200, 50);
                entry.setPrefSize(200, 50);

                TextField field = new TextField();
                field.setText(csvHeader);
                field.setEditable(false);
                field.setMinSize(150, 30);
                field.setPrefSize(150, 30);
                entryList.add(field);
                entry.getChildren().add(field);

                setOnDragOver(event -> {
                    if (event.getDragboard().hasFiles()) {
                        event.acceptTransferModes(TransferMode.ANY);
                    }
                    event.consume();
                });

                entry.setOnDragDropped(e -> {
                    String draggedDataJSON = e.getDragboard().getString();
                    Document draggedData = Document.parse(draggedDataJSON);

                    System.out.println("Drag dropped "+ draggedDataJSON);
                });

                /*ComboBox<String> columns = new ComboBox(defaultColumnDataSet);
                columns.setMinSize(150, 30);
                columns.setPrefSize(150, 30);
                columns.getSelectionModel().selectFirst();
                //columnSelectionList.add(columns);
                //entry.getChildren().add(columns);*/

                columnPane.getChildren().add(entry);
            });

            setCenter(columnPane);
            closeButton.setOnAction(e -> {
                columnSelectorList.remove(this);
                root.getChildren().remove(this);
            });




        }

        public File getFile() {
            return this.csvFile;
        }

        public HashMap<String, String> userEntryMap() {
            HashMap<String, String> map = new HashMap<>();

            for(int loop = 0; loop < entryList.size(); loop++) {
                String selectedColumn = columnSelectionList.get(loop).getSelectionModel().getSelectedItem();

                if(!selectedColumn.equals(IGNORE_STRING))
                        map.put(entryList.get(loop).getText(), selectedColumn);
            }
            return map;
        }


    }
}
