package cms.engine.tasks;

import cms.controller.CrawlerStatusController;
import cms.engine.connection.crawlserver.ConnectionManager;
import cms.engine.connection.crawlserver.ServerThread;
import cms.engine.connection.soup.Helper;
import cms.model.Arachnid;
import cms.model.Columns;
import cms.model.Task;
import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

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

    private class TaskThread extends Thread {
        public boolean threadActive = true;

        @Override
        public void run() {
            boolean notYetPrinted = true;
            while(threadActive) {
                Task nextTask = CrawlTaskManager.getTask();
                if(activeCrawlerList.size() > 0) {
                    Socket nextArachnidSocket = activeCrawlerList.get(roundRobinCounter).getSocketUsed();
                    if(nextTask == null || nextArachnidSocket == null) {
                        if(notYetPrinted) {
                            System.out.println("No Task/Crawler Available");
                            notYetPrinted = false;
                        }
                    }
                    else {
                        CrawlTaskManager.sendTask(nextArachnidSocket, nextTask);
                        notYetPrinted = true;
                    }
                }
                try { Thread.sleep(1000); } catch (InterruptedException exc) {}
            }
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

    public ArrayList<Arachnid> getActiveCrawlerList() {
        return activeCrawlerList;
    }
}
