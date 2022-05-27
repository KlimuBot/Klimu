package eus.klimu.notification.domain.service.definition;

import eus.klimu.notification.domain.model.NotificationType;

import java.util.List;

public interface NotificationTypeService {

    NotificationType getNotificationType(long id);
    NotificationType getNotificationType(String name);
    List<NotificationType> getAllNotificationTypes();
    List<NotificationType> getAllNotificationTypes(String type);
    NotificationType addNewNotificationType(NotificationType notificationType);
    List<NotificationType> addAllNotificationTypes(List<NotificationType> notificationTypes);
    NotificationType updateNotificationType(NotificationType notificationType);
    void deleteNotificationType(NotificationType notificationType);

}
