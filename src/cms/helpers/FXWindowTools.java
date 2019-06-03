package cms.helpers;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
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

}
