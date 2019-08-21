package cms.model.ui;

import cms.controller.DashboardController;
import cms.engine.connection.crawlserver.ConnectionManager;
import cms.engine.helpers.FXWindowTools;
import com.mongodb.MongoTimeoutException;
import com.mongodb.client.ListCollectionsIterable;
import javafx.application.Platform;
import org.bson.Document;

public class CrawledDataListLoader extends javafx.concurrent.Task{

    private DashboardController dashboardController;

    public CrawledDataListLoader(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    public void initiate() {
        Thread initializerThread = new Thread(this);
        initializerThread.setDaemon(true);
        initializerThread.start();
    }

    @Override
    protected Void call() {
        boolean retry = true;

        while(retry) {
            try {
                ConnectionManager connectionManager = new ConnectionManager();

                ListCollectionsIterable<Document> collectionsNames = connectionManager.UbuntuDB("dataselect").listCollections();
                for (Document collectionName : collectionsNames) {
                    if (collectionName != null) {
                        String name = collectionName.getString("name");
                        if(!name.equals("")) Platform.runLater(() -> {
                            Platform.runLater(() -> dashboardController.getCrawledDataList().getItems().add(name));
                        });
                    }
                }
                Platform.runLater(()-> dashboardController.getCrawledDataList().getSelectionModel().selectFirst());
                retry = false;
            } catch (NullPointerException | MongoTimeoutException exc) {
                System.out.println("Server Connection Failed!");
                FXWindowTools.timedDelay(300);
            }
        }
        return null;
    }
}