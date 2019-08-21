package cms.engine.connection.mq;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MessageQueueConnectionBuilder {

    private ConnectionFactory connectionFactory;

    public MessageQueueConnectionBuilder(String username, String password, String virtualHost, String host, int port) {
        connectionFactory = new ConnectionFactory();
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(virtualHost);
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
    }

    public Connection getConnection() throws IOException, TimeoutException {
        return connectionFactory.newConnection();
    }
}
