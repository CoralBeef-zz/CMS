package cms.engine.connection.crawlserver;

import cms.controller.DashboardController;
import cms.engine.tasks.CrawlTaskManager;
import cms.model.Arachnid;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class CrawlerStatusUpdate extends Task<Void> {

    private static final int STATUS_PORT = 6790;
    private DashboardController dashboardController;

    public CrawlerStatusUpdate(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    @Override
    protected Void call() throws Exception {
        ServerSocket serverSocket = new ServerSocket(STATUS_PORT);

        while(true) {
            System.out.println("Waiting for status update ");
            Socket activeSocket = serverSocket.accept();
            DataInputStream stream = new DataInputStream(activeSocket.getInputStream());

            String receivedData = stream.readUTF();
            Arachnid statusUpdate = new Gson().fromJson(receivedData, Arachnid.class);

            Platform.runLater(() -> dashboardController.updateCrawlerStatus(statusUpdate));
        }
    }
}
