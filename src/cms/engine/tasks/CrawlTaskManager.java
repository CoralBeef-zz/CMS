package cms.engine.tasks;

import cms.engine.connection.crawlserver.ConnectionManager;
import cms.model.Arachnid;
import cms.model.Task;
import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;

public class CrawlTaskManager {

    private final ArrayList<Arachnid> activeCrawlerList = new ArrayList<>();
    private static CrawlTaskManager single_instance = null;
    private TaskThread taskThread = new TaskThread();
    private int roundRobinCounter = 0;

    private CrawlTaskManager() {
        Thread taskActualThread = new Thread(taskThread);
        taskActualThread.setDaemon(true);
    }

    private class TaskThread extends javafx.concurrent.Task {
        public boolean threadActive = true;

        @Override
        protected Void call() {
            boolean notYetPrinted = true;
            while(threadActive) {
                Task nextTask = CrawlTaskManager.getTask();
                if(activeCrawlerList.size() > 0) {
                    Arachnid nextArachnid = nextAvailableArachnid();
                    Socket nextArachnidSocket = nextArachnid.getSocketUsed();

                    if(nextTask == null || nextArachnidSocket == null) {
                        if(notYetPrinted) {
                            System.out.println("No Task to Send!");
                            notYetPrinted = false;
                        }
                    }
                    else {
                        System.out.println("No Crawler Available!");
                        CrawlTaskManager.sendTask(nextArachnidSocket, nextTask);
                        notYetPrinted = true;
                    }
                }
                try { Thread.sleep(100); } catch (InterruptedException exc) {}
            }
            return null;
        }
    }



    public TaskThread getTaskThread() {
        return taskThread;
    }

    public static CrawlTaskManager getInstance() {
        if (single_instance == null) single_instance = new CrawlTaskManager();
        return single_instance;
    }

    public static String serializeTask(Task taskToSerialize) {
        Gson parser = new Gson();
        String serializedTask = parser.toJson(taskToSerialize);

        return serializedTask;
    }

    public static void sendTask(Socket socket, Task taskToSend) {
        try {
            String serializedTask = serializeTask(taskToSend);
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

            System.out.println("Sending: "+serializedTask);
            outputStream.writeUTF(serializedTask);
            outputStream.flush();


        } catch(IOException exc) {
            System.out.println(exc.toString());
        }
    }

    private Arachnid nextAvailableArachnid() {
        int currentCounter = roundRobinCounter;
        Arachnid nextArachnid = activeCrawlerList.get(roundRobinCounter);
        do {
            if (nextArachnid.getStatus().equals("AVAILABLE")) {
                return nextArachnid;
            } else {
                roundRobinCounter = ((roundRobinCounter + 1) < (activeCrawlerList.size())) ? (roundRobinCounter + 1) : 0;
                nextArachnid = activeCrawlerList.get(roundRobinCounter);
            }
        } while(roundRobinCounter != currentCounter);
        return null;
    }

    public static Task getTask() {
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
                    taskDocument.getString("pageType"),
                    taskDocument.getString("dataJSON")
            );

            collection.findOneAndUpdate(
                    eq("_id", recentTask.getId()),
                    combine(com.mongodb.client.model.Updates.set("status", "CRAWLING"))
            );

            return recentTask;
        } catch(NullPointerException exc) {
            return null;
        }
    }

    public static void uploadTask(Task task) {
        try {
            ConnectionManager connection_manager = new ConnectionManager();
            MongoCollection<Document> collection = connection_manager.UbuntuDB("dataselect_crawler")
                    .getCollection("tasks");

            Document doc = new Document();
            doc.put("_id", task.getId());
            doc.put("siteGroup", task.getSiteGroup());
            doc.put("source", task.getSource());
            doc.put("site", task.getSite());
            doc.put("pageType", task.getPageType());
            doc.put("status", "ON_QUEUE");
            doc.put("crawlerId", task.getCrawlerId());
            doc.put("dataJSON", task.getData());
            doc.put("dateCreated", task.getDateCreated());
            collection.insertOne(doc);
        } catch(Exception exc) { exc.printStackTrace(); }
    }

    public ArrayList<Arachnid> getActiveCrawlerList() {
        return activeCrawlerList;
    }
}
