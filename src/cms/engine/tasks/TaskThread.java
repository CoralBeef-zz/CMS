package cms.engine.tasks;

import cms.engine.connection.crawlserver.ConnectionManager;
import cms.engine.connection.crawlserver.ServerThread;
import cms.model.Arachnid;
import cms.model.Task;
import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;

public class TaskThread extends javafx.concurrent.Task<Void> {

    private boolean running = true;
    private ServerThread serverThread;
    private CopyOnWriteArrayList<Arachnid> roundRobin;
    private Iterator<Arachnid> roundRobinIterator;

    public TaskThread(ServerThread serverThread) {
        this.serverThread = serverThread;
        this.roundRobin = this.serverThread.getActiveCrawlers();
        this.roundRobinIterator = this.roundRobin.iterator();
    }

    public synchronized void activate() { running = true; }
    public synchronized void deactivate() { running = false; }

    @Override
    protected Void call() {
        while(true) {
            try {
                while(running) {
                    Task nextTask = getTask();
                    if(nextTask != null) {
                        try {
                            if(!roundRobinIterator.hasNext()) {
                                this.roundRobin = this.serverThread.getActiveCrawlers();
                                roundRobinIterator = roundRobin.iterator();
                            }
                        Arachnid arach = roundRobinIterator.next();
                        System.out.println("wut" + new Gson().toJson(arach));
                        System.out.println("ARACH: " + arach.getId());
                        CrawlTaskManager.sendTask(
                                arach.getSocketUsed(),
                                nextTask
                        );

                        } catch(Exception exc) { System.out.println(exc.toString()); }
                    }
                    Thread.sleep(100);
                }
                Thread.sleep(1000);
            } catch (InterruptedException exc) {}
        }
    }

    public Task getTask() {
        try {
            ConnectionManager collection_manager = new ConnectionManager();
            MongoCollection<Document> collection = collection_manager.UbuntuDB("dataselect_crawler")
                    .getCollection("tasks");
            Document taskDocument = collection.find(eq("status", "ON_QUEUE")).first();

            Task recentTask = new Task(
                    taskDocument.getObjectId("_id"),
                    taskDocument.getInteger("siteGroup"),
                    taskDocument.getString("source"),
                    taskDocument.getString("site"),
                    taskDocument.getString("partitionString"),
                    taskDocument.getString("pageType")
            );

            collection.findOneAndUpdate(
                    eq("_id", recentTask.getId()),
                    combine(com.mongodb.client.model.Updates.set("status", "CRAWLING"))
            );

            return recentTask;
        } catch(NullPointerException exc) {
            System.out.println("NO TASK AVAILABLE");
            //TODO: Send NO TASK notification here
            return null;
        }
    }
}
