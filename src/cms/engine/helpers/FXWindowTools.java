package cms.engine.helpers;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public abstract class FXWindowTools {
    public static void centerThisStage(Stage stage) {
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);
    }
    public static void fadeInNode(Node node) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(2000), node);
        tt.setFromX(-20);
        tt.setToX(0);
        tt.play();

        FadeTransition ft = new FadeTransition(Duration.millis(2000), node);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();
    }
    public static void setStaticSize(Double width, Double height, Region... regions) {
        for(Region region : regions) {
            region.setMinWidth(width);
            region.setPrefWidth(width);
            region.setMaxWidth(width);

            region.setMinHeight(height);
            region.setPrefHeight(height);
            region.setMaxHeight(height);
        }
    }


    public static void setEditable(boolean editable, TextField... fields) {
        for(TextField field : fields) field.setEditable(editable);
    }

    public static void timedDelay(int delay) {
        try {
            Thread.sleep(delay);
        } catch(InterruptedException exc) {}
    }

    public static void openPage(Stage stage, Application application) {
        try {
            application.start(stage);
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    public static void initializeMainStage(Application application, Stage stage, String fxmlPath) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(application.getClass().getResource(fxmlPath));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

            FXWindowTools.centerThisStage(stage);
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    public static class ProgressForm {
        private final Stage dialogStage;
        private final ProgressBar pb = new ProgressBar();
        private final ProgressIndicator pin = new ProgressIndicator();

        public ProgressForm() {
            dialogStage = new Stage();
            dialogStage.initStyle(StageStyle.UTILITY);
            dialogStage.setResizable(false);
            dialogStage.initModality(Modality.APPLICATION_MODAL);

            // PROGRESS BAR
            final Label label = new Label();
            label.setText("Loading..");

            pb.setProgress(-1F);
            pin.setProgress(-1F);

            final HBox hb = new HBox();
            hb.setMinSize(250,70);
            hb.setPrefSize(250,70);

            hb.setSpacing(5);
            hb.setAlignment(Pos.CENTER);
            hb.getChildren().addAll(label,pb, pin);

            Scene scene = new Scene(hb);
            dialogStage.setScene(scene);
        }

        public void activateProgressBar(final Task<?> task)  {
            pb.progressProperty().bind(task.progressProperty());
            pin.progressProperty().bind(task.progressProperty());
            dialogStage.show();
        }

        public Stage getDialogStage() {
            return dialogStage;
        }
    }
}
