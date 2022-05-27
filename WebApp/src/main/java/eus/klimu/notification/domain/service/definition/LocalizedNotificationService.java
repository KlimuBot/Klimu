package eus.klimu.notification.domain.service.definition;

import eus.klimu.location.domain.model.Location;
import eus.klimu.notification.domain.model.LocalizedNotification;
import eus.klimu.notification.domain.model.NotificationType;

import java.util.List;

public interface LocalizedNotificationService {

    LocalizedNotification getLocalizedNotification(long id);
    LocalizedNotification getLocalizedNotification(Location location, NotificationType notificationType);
    List<LocalizedNotification> getAllLocalizedNotifications();
    List<LocalizedNotification> getAllLocalizedNotifications(Location location);
    List<LocalizedNotification> getAllLocalizedNotifications(NotificationType notificationType);
    LocalizedNotification addNewLocalizedNotification(LocalizedNotification localizedNotification);
    List<LocalizedNotification> addAllLocalizedNotifications(List<LocalizedNotification> localizedNotifications);
    LocalizedNotification updateLocalizedNotification(LocalizedNotification localizedNotification);
    void deleteLocalizedNotification(LocalizedNotification localizedNotification);

}
