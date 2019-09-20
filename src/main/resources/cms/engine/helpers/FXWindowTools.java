package main.resources.cms.engine.helpers;

import main.resources.cms.model.Columns;
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
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static void sendPost(String api, String body) {
        System.out.println("Sending post.. "+body);
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(api);

            StringEntity entity = new StringEntity(body, StandardCharsets.UTF_8);
            httpPost.setEntity(entity);
            httpPost.setHeader("Content-type", "application/json");

            CloseableHttpResponse response = client.execute(httpPost);
            if(response.getStatusLine().getStatusCode() == 200){
                System.out.println("POST request sent.");
            }else{
                System.out.println("There was an error in the request " + response.toString());
            }

            client.close();
        } catch (IOException e) {
            e.printStackTrace();
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


    public static String removeString(String pattern, String text) {
        return text.replaceAll(pattern, "");
    }

    public static String extractFirstMatch(String pattern, String text) {
        Matcher matcher = Pattern.compile(pattern).matcher(text);
        if (matcher.find()) return matcher.group(0);
        else return "";
    }

    public static String removeZipcode(String text) {
        return removeString("〒\\d{3}-\\d{4}", text);
    }

    public static String extractPrefectureFromAddress(String address) {
        return extractFirstMatch("((?:鹿児島県)|(?:神奈川県)|(?:和歌山県))|((\\S){2}(都|道|県|府))", address);
    }

    public static HashMap<Columns, String> splittedAddress(String address) {
        HashMap<Columns, String> list = new HashMap<>();

        address.replaceAll("[\\s ]","");

        address = removeZipcode(address);
        list.put(Columns.PREFECTURE, extractPrefectureFromAddress(address));
        address = address.replaceFirst(extractPrefectureFromAddress(address),"").trim();
        address = address.replaceAll(" ", "");
        address = address.split("[\\. \\n<]")[0];
        boolean found=false;

        int infinite_loop_checker = 0;
        do {
            Matcher matcher = Pattern.compile("(.+郡)|(.+市)|(.+区)|(.+町)|(.+村)|(.+丁目)").matcher(address);
            if(matcher.find()) {
                String partitioned_address = matcher.group(0);
                Columns key_for_this_partition = null;
                if(partitioned_address.substring(partitioned_address.length() - 2).matches("丁目"))
                    key_for_this_partition = Columns.DISTRICT;
                else switch (partitioned_address.substring(partitioned_address.length() - 1)) {
                    case("郡"): key_for_this_partition = Columns.CITY; break;
                    case("市"): key_for_this_partition = Columns.CITY; break;
                    case("区"): key_for_this_partition = Columns.WARD; break;
                    case("町"): key_for_this_partition = Columns.TOWN; break;
                    case("村"): key_for_this_partition = Columns.TOWN; break;
                }

                list.put(key_for_this_partition, partitioned_address);
                address = address.replaceFirst(partitioned_address,"");
                found = Pattern.compile("(.+郡)|(.+市)|(.+区)|(.+町)|(.+村)|(.+丁目)").matcher(address).find();
            }
            infinite_loop_checker++;
            if(infinite_loop_checker>10) {
                break;
            }
        } while(found);

        list.put(Columns.BLOCK, address);

        return list;
    }
}
