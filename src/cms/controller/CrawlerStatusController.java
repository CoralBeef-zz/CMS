package cms.controller;

import cms.engine.connection.crawlserver.ServerThread;
import cms.engine.tasks.CrawlTaskManager;
import cms.model.Arachnid;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;

import static cms.engine.tasks.CrawlTaskManager.getInstance;

public class CrawlerStatusController {

    private ServerThread serverThread;
    private CrawlTaskManager taskManager;

    @FXML
    private FlowPane activeCrawlerListPane;

    @FXML
    private TextArea systemTerminal;

    @FXML
    public void initialize() {
        taskManager = getInstance();

        Thread serverThread = new Thread(this.serverThread);
        serverThread.setDaemon(true);
        serverThread.start();

        Thread taskThread = new Thread(this.taskManager.getTaskThread());
        taskThread.setDaemon(true);
        taskThread.start();
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

    public void activateCrawler(Arachnid arachnid, CrawlTaskManager taskManager) {
        System.out.println("Adding a new crawler: "+arachnid.getIpAddress());

        taskManager.getActiveCrawlerList().add(arachnid);
        CrawlerBox box = new CrawlerBox(arachnid.getId().toString());
        box.addThisToContainer(activeCrawlerListPane);
    }
}
