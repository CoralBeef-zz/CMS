package cms.engine.connection.mq;

import cms.model.Task;
import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.GetResponse;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class TaskMQFeeder {

    //private final GlobalTerminal terminal = GlobalTerminal.INSTANCE;

    private static final String MODULE_NAME = "ARACHNID_TASK";
    private Connection connection;
    private Channel channel;

    public TaskMQFeeder() {
        try {
            MessageQueueConnectionBuilder messageQueueConnectionBuilder = new MessageQueueConnectionBuilder(
                    "dataselect",
                    "d4t4s3l3ct",
                    "/",
                    "192.168.0.159",
                    5672
            );

            connection = messageQueueConnectionBuilder.getConnection();
            channel = connection.createChannel();
            channel.confirmSelect();
        } catch(TimeoutException exc) {
            exc.printStackTrace();
        } catch(IOException exc) {
            exc.printStackTrace();
        }
    }

    public Task requestTask() {
        try {
            channel.basicQos(1);
            channel.queueDeclarePassive(MODULE_NAME);

            GetResponse response = channel.basicGet(MODULE_NAME, false);
            if(response != null) {
                String message = new String(response.getBody(), StandardCharsets.UTF_8);
                Task task = new Gson().fromJson(message, Task.class);

                channel.basicAck(response.getEnvelope().getDeliveryTag(), false);

                System.out.println("Task Received: " + task.getSite());
                return task;
            }
            return null;
        } catch (IOException exc) { return null;}
    }

    public void sendTask(Task task) {
        String message = new Gson().toJson(task);

        try {
            while (true) {
                channel.queueDeclare(MODULE_NAME, true, false, false, localArguments());
                channel.basicPublish("", MODULE_NAME, true, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
                if (channel.waitForConfirms()) break;

                System.out.println("Nack'ed! Retrying in 5s.");
                Thread.sleep(5000);
            }
        } catch(IOException exc) {

        } catch(InterruptedException exc) {}
    }

    public void close() {
        try {
            channel.close();
            connection.close();
        } catch (IOException exc) {}
    }

    private static Map<String, Object> localArguments() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-max-length-bytes", 1000000);
        arguments.put("x-overflow", "reject-publish");
        return arguments;
    }
}
