package eus.klimu.notification.domain.service.definition;

import eus.klimu.location.domain.model.Location;
import eus.klimu.notification.domain.model.Notification;
import eus.klimu.notification.domain.model.NotificationType;

import java.util.Date;
import java.util.List;

public interface NotificationService {

    Notification getNotificationById(long id);
    List<Notification> getAllNotifications(Location location);
    List<Notification> getAllNotifications(NotificationType type);
    List<Notification> getAllNotifications(Date startDate, Date endDate);
    List<Notification> getNotificationsByDateBetween(Location location, Date startDate, Date endDate);
    List<Notification> getNotificationsByDateBetween(NotificationType type, Date startDate, Date endDate);
    Notification addNewNotification(Notification notification);
    List<Notification> addAllNotifications(List<Notification> notifications);
    Notification updateNotification(Notification notification);
    void deleteNotification(Notification notification);

}
