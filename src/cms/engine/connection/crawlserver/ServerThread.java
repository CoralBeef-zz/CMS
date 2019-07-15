package cms.engine.connection.crawlserver;

import cms.controller.CrawlerStatusController;
import cms.engine.tasks.CrawlTaskManager;
import cms.model.Arachnid;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServerThread extends Task<Void> {

    private ServerSocket serverSocket;

    private static final int port = 6789;
    private CrawlerStatusController crawlerStatusController;

    private ServerThread(){}
    public ServerThread(CrawlerStatusController activeCrawlerStatusController) {
        try {
            serverSocket = new ServerSocket(port);
        } catch(IOException exc) {
            System.out.println("Failed: "+exc.toString());
        }
        this.crawlerStatusController = activeCrawlerStatusController;
    }

    @Override
    protected Void call() {
        while(initializeConnection()) {
            try {
                System.out.println("Server initialization failed!");
                Thread.sleep(200);
            } catch(InterruptedException exc) {}
        }
        System.out.println("Connected!");

        Thread serverThread = new Thread(new ServerThread());
        serverThread.setDaemon(true);
        serverThread.start();
        return null;
    }

    private boolean initializeConnection() {
        try {
            Socket activeSocket = serverSocket.accept();
            activateCrawlerFromSocket(activeSocket);
            System.out.println("Server Ready");
            return false;
        } catch (IOException exc) {
            System.out.println("Failed: "+exc.toString());
            return true;
        }
    }

    private void activateCrawlerFromSocket(Socket sourceSocket) {
        try {
            DataInputStream inputStream = new DataInputStream(sourceSocket.getInputStream());
            String receivedArachnidJSON = inputStream.readUTF();
            Arachnid receivedArachnid = new Gson().fromJson(receivedArachnidJSON, Arachnid.class);
            receivedArachnid.setSocketUsed(sourceSocket);

            Platform.runLater(() -> crawlerStatusController.activateCrawler(receivedArachnid, CrawlTaskManager.getInstance()));
        } catch(IOException exc) {

        }
    }

}
