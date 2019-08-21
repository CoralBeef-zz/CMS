package cms.engine.helpers;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.stage.Screen;
import javafx.stage.Stage;
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

}
