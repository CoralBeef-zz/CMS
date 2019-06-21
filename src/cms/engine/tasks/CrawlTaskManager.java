package cms.engine.tasks;

import cms.engine.connection.crawlserver.ServerThread;
import cms.model.Task;
import com.google.gson.Gson;
import org.bson.types.ObjectId;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class CrawlTaskManager {


    private ServerThread serverThread;

    private TaskThread taskThread;

    private static CrawlTaskManager single_instance = null;
    private CrawlTaskManager(ServerThread serverThread) {
        this.serverThread = serverThread;
        this.taskThread = new TaskThread(serverThread);

        Thread taskActualThread = new Thread(taskThread);
        taskActualThread.setDaemon(true);
        taskActualThread.start();
    }

    public static CrawlTaskManager initInstance(ServerThread serverThread)
    {
        if (single_instance == null) single_instance = new CrawlTaskManager(serverThread);
        return single_instance;
    }
    public static CrawlTaskManager getInstance() {
        return single_instance;
    }

    public void startTaskThread() {
        taskThread.activate();
    }

    public void stopTaskThread() {
        taskThread.deactivate();
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

    public static Task createNewTask(Integer siteGroup, String source, String site, String partitionString, String pageType) {
        return new Task(
                new ObjectId(),
                siteGroup,
                source,
                site,
                partitionString,
                pageType
        );
    }
}
