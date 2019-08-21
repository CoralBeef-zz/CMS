package cms.model.ui;

import cms.engine.uploader.CSVUploader;
import cms.model.Columns;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import javax.xml.soap.Text;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class CsvColumnSelector extends GridPane {
    public HashMap<String, ComboBox> OUT_COLUMNS;
    private TextField collectionNameField;
    private ComboBox categoryBox;
    private HashMap<String, ArrayList<Columns>> columnData;

    private Button uploadCSVButton;
    private Button closeButton;

    public CsvColumnSelector(Pane parentPane, ArrayList<String> headers, TextField uploadCSVTextField) {

        try {
            OUT_COLUMNS = new HashMap<>();
            collectionNameField = new TextField();
            categoryBox = new ComboBox();
            columnData = Columns.getAllColumns();
            uploadCSVButton = new Button("Upload");
            closeButton = new Button("Close");

            for (String columnDataCategories : columnData.keySet()) categoryBox.getItems().add(columnDataCategories);

            int rowcount = 0;
            setId("csvColumnSelector");
            setVgap(5);
            setHgap(10);

            Label collectionNameLabel = new Label("Collection Name: ");
            add(collectionNameLabel, 0, rowcount);
            add(collectionNameField, 1, rowcount, 2, 1);
            rowcount++;

            add(categoryBox, 2, rowcount);
            add(uploadCSVButton, 2, (rowcount + 1));
            add(closeButton, 2, (rowcount + 2));
            for (String header : headers) {
                TextField sourceField = new TextField();
                sourceField.setText(header);
                sourceField.setEditable(false);

                ComboBox outField = new ComboBox();

                OUT_COLUMNS.put(header, outField);
                add(sourceField, 0, rowcount);
                add(outField, 1, rowcount);
                rowcount++;
            }

            reloadBoxes(columnData.keySet().iterator().next());
            categoryBox.getSelectionModel().selectFirst();

            categoryBox.setOnAction(e -> {
                String selectedCategory = (String) categoryBox.getSelectionModel().getSelectedItem();
                reloadBoxes(selectedCategory);
            });

            uploadCSVButton.setOnAction(e -> upload(uploadCSVTextField.getText()));
            closeButton.setOnAction(e -> closeThis(parentPane));


        } catch (Exception exc) {
            closeThis(parentPane);
        }
    }

    private void closeThis(Pane parentPane) {
        getChildren().clear();
        Node previousColSelector = parentPane.lookup("#csvColumnSelector");
        if(previousColSelector != null) parentPane.getChildren().remove(previousColSelector);
    }

    private void upload(String sourceFile) {
        try {
            File csvFile = new File(sourceFile);
            CSVUploader csvUploader = new CSVUploader(csvFile);

            HashMap<String, String> OUT_COLUMNS_CONVERTED = new HashMap<>();
            OUT_COLUMNS.forEach((key, value) -> {
                String selectedColumnName = (String) value.getSelectionModel().getSelectedItem();
                if(!selectedColumnName.equals("IGNORE"))
                    OUT_COLUMNS_CONVERTED.put(key, selectedColumnName);
            });

            String selectedCategory = (String) (categoryBox.getSelectionModel().getSelectedItem());
            Integer selectedCategoryIndex = Columns.convertCategoryToIndex(selectedCategory);
            csvUploader.upload(collectionNameField.getText(), selectedCategoryIndex, OUT_COLUMNS_CONVERTED);
        } catch (Exception exc) {
            //TODO: Error Message Here
            getChildren().clear();
        }
    }

    private void reloadBoxes(String selectedCategory) {
        OUT_COLUMNS.forEach((key, value) -> {
            ArrayList<Columns> columns = columnData.get(selectedCategory);
            ArrayList<String> columnNames = Columns.colArrayToString(columns);
            value.getItems().add("IGNORE");
            for(String columnName : columnNames)
                value.getItems().add(columnName);
            value.getSelectionModel().selectFirst();
        });
    }
}