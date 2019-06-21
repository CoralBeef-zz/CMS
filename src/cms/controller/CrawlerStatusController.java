package cms.controller;

import cms.engine.connection.crawlserver.ServerThread;
import cms.model.Arachnid;
import cms.engine.tasks.CrawlTaskManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;

import java.util.ArrayList;

import static cms.engine.tasks.CrawlTaskManager.initInstance;

public class CrawlerStatusController {

    private ServerThread serverThread;
    private CrawlTaskManager taskManager;
    private final ArrayList<Arachnid> activeCrawlerList = new ArrayList<>();

    @FXML
    private FlowPane activeCrawlerListPane;

    @FXML
    private TextArea systemTerminal;

    @FXML
    private Button runCrawlerButton;
    private boolean running = true;

    @FXML
    public void initialize() {
        serverThread = new ServerThread(this);
        taskManager = initInstance(serverThread);

        Thread serverThread = new Thread(this.serverThread);
        serverThread.setDaemon(true);
        serverThread.start();

        runCrawlerButton.setOnAction(e -> {
            if(running) {
                System.out.println("STOPPED");
                CrawlTaskManager.getInstance().stopTaskThread();
                running = false;
            } else {
                System.out.println("STARTED");
                CrawlTaskManager.getInstance().startTaskThread();
                running = true;
            }
        });
    }

    public void activateCrawler(Arachnid arachnid) {
        System.out.println("Adding a new crawler: "+arachnid.getIpAddress());

        this.activeCrawlerList.add(arachnid);
        CrawlerBox box = new CrawlerBox(arachnid.getId().toString());
        box.addThisToContainer(activeCrawlerListPane);
    }
    public void deactivateCrawler(String id) {
        System.out.println("Removing crawler");

    }

    public CrawlTaskManager getTaskManager() {
        return taskManager;
    }

    public class CrawlerBox extends Button {
        private String id = "";

        private CrawlerBox() {}
        public CrawlerBox(String id) {
            this.id = id;

            setText(id);
            getStyleClass().add("crawlerBox-Red");
            setPrefWidth(100);
            setPrefHeight(100);
        }

        public void addThisToContainer(Pane pane) {
            pane.getChildren().add(this);
        }

        public void removeThisFromContainer(FlowPane pane) {
            pane.getChildren().remove(this);
        }
    }

    @FXML
    public void shutdown() {
        if(serverThread != null) serverThread.close();
    }
}
