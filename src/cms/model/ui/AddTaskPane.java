package cms.model.ui;

import cms.controller.DashboardController;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import static cms.engine.helpers.FXWindowTools.setStaticSize;

public class AddTaskPane extends GridPane {
    private TextField targetSiteField;
    private TextField targetPartitionField;

    private Button addTaskButton;

    public AddTaskPane(DashboardController parentController) {
        super();
        Pane parentPane = parentController.getTaskRootPane();

        setPadding(new Insets(5,5,5,5));

        setStaticSize(parentPane.getPrefWidth()*0.9, 100.0, this);
        setHgap(5);
        setVgap(5);

        targetSiteField = new TextField();
        targetPartitionField = new TextField();
        setStaticSize(parentPane.getPrefWidth()*0.4, 40.0, targetSiteField, targetPartitionField);

        Label targetSiteLabel = new Label("TARGET SITE: ");
        Label targetPartitionLabel = new Label("TARGET PARTITION: ");
        setStaticSize(parentPane.getPrefWidth()*0.2, 40.0, targetSiteLabel, targetPartitionLabel);

        addTaskButton = new Button("Add New Task");
        setStaticSize(parentPane.getPrefWidth()*0.2, 80.0, addTaskButton);

        add(targetSiteLabel, 0,0);
        add(targetSiteField, 1, 0);
        add(targetPartitionLabel, 0, 1);
        add(targetPartitionField, 1, 1);

        add(addTaskButton, 2, 0, 1, 2);

        addTaskButton.setOnAction(event -> parentController.addTaskGroup(parentPane, targetSiteField.getText(), targetPartitionField.getText()));
    }



    public TitledPane onTitledPane(String title) {
        TitledPane titledPane = new TitledPane(title, this);
        titledPane.setCollapsible(false);
        return titledPane;
    }
}