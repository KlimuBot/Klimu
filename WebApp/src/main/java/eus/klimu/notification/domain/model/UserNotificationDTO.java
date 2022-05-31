package eus.klimu.notification.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
public class UserNotificationDTO implements Serializable {

    private Long id;
    private Long channelId;
    private Collection<Long> localizedNotifications;

    public static UserNotificationDTO fromUserNotification(UserNotification userNotification) {
        UserNotificationDTO userNotificationDTO = new UserNotificationDTO();
        Collection<Long> notificationIds = new ArrayList<>();

        userNotificationDTO.setId(userNotification.getId());
        userNotificationDTO.setChannelId(userNotification.getChannel().getId());
        userNotification.getNotifications().forEach(n -> notificationIds.add(n.getId()));
        userNotificationDTO.setLocalizedNotifications(notificationIds);

        return userNotificationDTO;
    }

}
