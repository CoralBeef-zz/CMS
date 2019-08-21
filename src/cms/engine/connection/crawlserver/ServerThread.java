package cms.engine.connection.crawlserver;

import cms.controller.CrawlerStatusController;
import cms.controller.DashboardController;
import cms.model.Arachnid;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread extends Task<Void> {

    private ServerSocket serverSocket;

    private static final int SERVER_PORT = 6790;
    //private CrawlerStatusController crawlerStatusController;
    private DashboardController dashboardController;

    private ServerThread(){}
    @Deprecated public ServerThread(CrawlerStatusController activeCrawlerStatusController) {}
    public ServerThread(DashboardController dashboardController) {
        try {
            serverSocket = new ServerSocket(SERVER_PORT);
        } catch(IOException exc) {
            System.out.println("Failed: "+exc.toString());
        }
        //this.crawlerStatusController = activeCrawlerStatusController;
        this.dashboardController = dashboardController;
    }

    @Override
    protected Void call() {
        boolean never_done = true;
        while(true) {
            try {
                Socket activeSocket = serverSocket.accept();
                receiveArachnidFromSocket(activeSocket);
                System.out.println("Server Ready");

                if(never_done) {
                    Thread serverThread = new Thread(new ServerThread());
                    serverThread.setDaemon(true);
                    serverThread.start();
                    never_done = false;
                }
            } catch (IOException exc) { exc.printStackTrace(); }
        }

        /*Thread crawlerStatusUpdateThread = new Thread(new CrawlerStatusUpdate(dashboardController));
        crawlerStatusUpdateThread.setDaemon(true);
        crawlerStatusUpdateThread.start();*/
    }

    private void receiveArachnidFromSocket(Socket sourceSocket) {
        try {
            DataInputStream inputStream = new DataInputStream(sourceSocket.getInputStream());
            String receivedArachnidJSON = inputStream.readUTF();
            Arachnid receivedArachnid = new Gson().fromJson(receivedArachnidJSON, Arachnid.class);
            receivedArachnid.setSocketUsed(sourceSocket);

            Platform.runLater(() -> dashboardController.activateCrawler(receivedArachnid));
        } catch (IOException exc) {
        }
    }
}
