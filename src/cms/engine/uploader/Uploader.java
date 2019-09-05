package cms.engine.uploader;

import cms.engine.helpers.FXWindowTools;
import com.google.common.collect.Lists;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import cms.engine.connection.crawlserver.ConnectionManager;
import cms.model.Information;
import com.mongodb.client.model.Filters;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.bson.Document;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class Uploader extends Task {

    private final String SOURCE_DB;
    private final Label PROGRESS_LABEL;

    private int progress_max = 0;


    private final String serverIp = "157.65.27.69:3301"; // Staging server
    //private final String serverIp = "dataselect.jp:3301"; // Production server

    public Uploader(Label progressLabel, String source_db) {
        this.SOURCE_DB = source_db;
        //this.ACTIVATED_BUTTON = activatedButton;
        this.PROGRESS_LABEL = progressLabel;
    }

    @Override
    protected Void call() {
        upload();
        return null;
    }

    public void upload() {
        int PROGRESS_CURRENT = 0;

        updateProgressText("Uploading..");

        ConnectionManager collection_to_get_manager = new ConnectionManager();
        MongoCollection<Document> collection_to_get = collection_to_get_manager.UbuntuDB("dataselect")
                .getCollection(this.SOURCE_DB);

        MongoCursor<Document> info_list = collection_to_get.find().noCursorTimeout(true).iterator();

        long TOTAL = collection_to_get.countDocuments();

        progress_max = (int) collection_to_get.countDocuments();
        boolean doOnce = true;

        while (info_list.hasNext()) {
            if(!isCancelled()) {
                Document download_info = info_list.next();

                ConnectionManager collection_to_upload_manager = new ConnectionManager();
                //MongoCollection<Document> collection_to_upload = collection_to_upload_manager.AWSDB("MasterDB")
                //        .getCollection("information");
                MongoCollection<Document> collection_to_upload = collection_to_upload_manager.AtlasDB("master-db")
                        .getCollection("information");

                if (doOnce) {
                    if (sourceDBDoesNotExist(collection_to_upload, this.SOURCE_DB))
                        addNewWebsite(this.SOURCE_DB, download_info.getInteger("siteGroup"));

                    doOnce = false;
                }

                String source = (String) download_info.get("source");
                String site = (String) download_info.get("site");
                Integer siteGroup = (Integer) download_info.get("siteGroup");

                Information upload_info = new Information(source, site, siteGroup);
                ((Map<String, String>) download_info.get("data"))
                        .forEach((key, value) -> {
                            if (key.equals("mailAddress")) upload_info.addData(key, value.toLowerCase());
                            else upload_info.addData(key, value);
                        });

                updateProgressText(PROGRESS_CURRENT + "/" + TOTAL);
                System.out.println(PROGRESS_CURRENT + ") UPLOADING DATA FROM SOURCE " + upload_info.getSource());
                upload_info.insertThisToCollection(collection_to_upload);
                //upload_info.insertDataOnlyToCollection(collection_to_upload);

                PROGRESS_CURRENT++;
                updateProgress(PROGRESS_CURRENT, TOTAL);

                collection_to_upload_manager.getConnectionBuilder().deactivateConnection();
            }
        }
        collection_to_get_manager.getConnectionBuilder().deactivateConnection();
        if(isCancelled()) {
            System.out.println("CANCELLED!");
        } else {
            System.out.println("UPLOAD SUCCESSFUL! " + PROGRESS_CURRENT + " DATA UPLOADED! ");
            updateProgressText("Success!");
        }

        info_list.close();

        updateProgress(PROGRESS_CURRENT, PROGRESS_CURRENT);
    }

    public boolean sourceDBDoesNotExist(MongoCollection<Document> collection, String sourceDB) {
        FindIterable<Document> foundDocument = collection.find(Filters.eq("name", sourceDB)).limit(1);
        return foundDocument.first() == null;
    }

    public void addNewWebsite(String siteName, Integer categoryId) {
        String input = "{ " +
                "\"website\": \""+siteName+"\", " +
                "\"category_id\": "+categoryId +
                " }";

        sendPost("http://"+serverIp+"/category/website/", input);
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

    private void updateProgressText(String status) {
        Platform.runLater(() -> this.PROGRESS_LABEL.setText(status));
    }


}
