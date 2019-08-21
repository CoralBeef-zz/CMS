package cms.model.ui;

import cms.model.Task;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import static cms.engine.helpers.FXWindowTools.setEditable;
import static cms.engine.helpers.FXWindowTools.setStaticSize;

public class TaskGroupPane extends GridPane {
    ProgressBar progressBar = new ProgressBar(0);

    public TaskGroupPane(Pane parentPane, Task task) {
        super();
        setPadding(new Insets(10,5,10,5));
        setStaticSize(parentPane.getPrefWidth()*0.9, 150.0, this);
        setHgap(10);
        setVgap(5);

        TextField targetSiteField = new TextField(); targetSiteField.setText(task.getSite());
        TextField targetPartitionField = new TextField(); targetPartitionField.setText(task.getSource());
        TextField dataCrawledField = new TextField();
        TextField totalDataField = new TextField();
        TextField successField = new TextField();
        TextField startedAtField = new TextField();
        TextField etaField = new TextField();
        TextField errorField = new TextField();
        setStaticSize(parentPane.getPrefWidth()*0.25, 20.0,
                targetSiteField, targetPartitionField, dataCrawledField, totalDataField,
                successField, startedAtField, etaField, errorField);
        setEditable(false,
                targetSiteField, targetPartitionField, dataCrawledField, totalDataField,
                successField, startedAtField, etaField, errorField);

        Label targetSiteLabel = new Label("TARGET SITE: ");
        Label targetPartitionLabel = new Label("TARGET PARTITION: ");
        Label dataCrawledLabel = new Label("DATA CRAWLED: ");
        Label totalDataLabel = new Label("TOTAL DATA: ");
        Label successLabel = new Label("SUCCESS: ");
        Label startedAtLabel = new Label("STARTED AT: ");
        Label etaLabel = new Label("ETA: ");
        Label errorLabel = new Label("ERROR: ");

        setStaticSize(parentPane.getPrefWidth()*0.15, 15.0,
                targetSiteLabel, targetPartitionLabel, dataCrawledLabel, totalDataLabel,
                successLabel, startedAtLabel, etaLabel, errorLabel);

        int rowcount = 1;
        add(targetSiteLabel, 0,rowcount);
        add(targetSiteField, 1, rowcount);
        add(startedAtLabel, 2, rowcount);
        add(startedAtField, 3, rowcount); rowcount++;
        add(targetPartitionLabel, 0,rowcount);
        add(targetPartitionField, 1, rowcount);
        add(etaLabel, 2, rowcount);
        add(etaField, 3, rowcount); rowcount++;
        add(dataCrawledLabel, 0,rowcount);
        add(dataCrawledField, 1, rowcount);
        add(totalDataLabel, 2, rowcount);
        add(totalDataField, 3, rowcount); rowcount++;
        add(successLabel, 0,rowcount);
        add(successField, 1, rowcount);
        add(errorLabel, 2, rowcount);
        add(errorField, 3, rowcount); rowcount++;
        Label spacer = new Label(); setStaticSize(parentPane.getPrefWidth()*0.9, 10.0, spacer);
        add(spacer, 0,rowcount, 3, 1); rowcount++;

        setStaticSize(parentPane.getPrefWidth()*0.9, 10.0, progressBar);
        add(progressBar, 0, rowcount, 3, 1);
    }

    public TitledPane onTitledPane(String title) {
        return new TitledPane(title, this);
    }
}
