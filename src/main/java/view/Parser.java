package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Parser extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(this.getClass().getResource("/UI/FXML/ParserUI.fxml"));
        primaryStage.setTitle("Data Select Uploader");
        primaryStage.setScene(new Scene(root, 500, 600));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
