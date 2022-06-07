package eus.klimu.klimudesktop.connection;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import eus.klimu.klimudesktop.app.notification.Notification;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

@Slf4j
public class RabbitConnector extends Thread {

    private static final String SERVER_IP = "klimu.eus";
    private static final Integer SERVER_PORT = 5672;
    private static final String EXCHANGE_NAME = "desktop-notification";

    private static final String LOGIN_USERNAME = "klimu.admin";
    private static final String LOGIN_PASSWORD = "klimu@admin";

    private static volatile  boolean stop = false;

    private final ConnectionFactory factory = new ConnectionFactory();
    private NotificationConsumer notificationConsumer = null;
    private final String username;

    public RabbitConnector(String username) {
        this.username = username;
        factory.setHost(SERVER_IP);
        factory.setPort(SERVER_PORT);
        factory.setUsername(LOGIN_USERNAME);
        factory.setPassword(LOGIN_PASSWORD);
    }

    public static void stopConnection() {
        stop = true;
    }

    public List<Notification> getNotifications() {
        if (notificationConsumer != null) {
            return notificationConsumer.getConcurrentNotifications();
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public void run() {
        Channel channel = null;

        log.info("Trying to connect with the RabbitMQ server");
        try (Connection connection = factory.newConnection()) {
            // Create the connection.
            channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, "direct", true);

            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, EXCHANGE_NAME, username);

            notificationConsumer = new NotificationConsumer(channel);
            String tag = channel.basicConsume(queueName, true, notificationConsumer);

            log.info("The connection was successful");
            // Keep the connection alive.
            do {
                Thread.yield();
            } while (!stop);

            // Stop the connection.
            channel.basicCancel(tag);

        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        } catch (TimeoutException timeEx) {
            log.error("Could not connect with the RabbitMQ server");
        } finally {
            // CLose the channel.
            if (channel != null) {
                try {
                    channel.close();
                } catch (IOException | TimeoutException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
