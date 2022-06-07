package eus.klimu.klimudesktop.connection;

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import eus.klimu.klimudesktop.app.notification.Notification;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
public class NotificationConsumer extends DefaultConsumer {

    private final LinkedList<Notification> concurrentNotifications = new LinkedList<>();
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock(true);
    private final Gson gson = new Gson();

    public NotificationConsumer(Channel channel) {
        super(channel);
    }

    private void setConcurrentNotification(Notification notification) {
        rwLock.writeLock().lock();
        concurrentNotifications.add(notification);
        rwLock.writeLock().unlock();
    }

    public List<Notification> getConcurrentNotifications() {
        rwLock.readLock().lock();
        List<Notification> notifications = new ArrayList<>(concurrentNotifications);
        rwLock.readLock().unlock();

        // Remove the first n elements of the list, where n = the number of
        // notifications that have been collected.
        rwLock.writeLock().lock();
        for (int i = 0; i < notifications.size(); i++) {
            concurrentNotifications.removeFirst();
        }
        rwLock.writeLock().unlock();

        return notifications;
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
            throws IOException {
        Notification notification = gson.fromJson(new String(body, StandardCharsets.UTF_8), Notification.class);

        log.info("A new notification has been received: {} at {}",
                notification.getType().getName(),
                notification.getLocation()
        );
        this.setConcurrentNotification(notification);
    }
}
