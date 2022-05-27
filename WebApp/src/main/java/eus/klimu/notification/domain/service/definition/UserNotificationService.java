package eus.klimu.notification.domain.service.definition;

import eus.klimu.channel.domain.model.Channel;
import eus.klimu.notification.domain.model.LocalizedNotification;
import eus.klimu.notification.domain.model.UserNotification;

import java.util.List;

public interface UserNotificationService {

    UserNotification getUserNotification(long id);
    List<UserNotification> getUserNotifications();
    List<UserNotification> getUserNotificationsByChannel(Channel channel);
    List<UserNotification> getUserNotificationsByNotification(LocalizedNotification localizedNotification);
    UserNotification addNewUserNotification(UserNotification userNotification);
    List<UserNotification> addAllUserNotifications(List<UserNotification> userNotifications);
    UserNotification updateUserNotification(UserNotification userNotification);
    void deleteUserNotifications(UserNotification userNotification);

}
