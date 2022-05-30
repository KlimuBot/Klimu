package eus.klimu.notification.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class NotificationTypeDTO implements Serializable {

    private long id;
    private String name;
    private String description;
    private String type;

    public static NotificationTypeDTO fromNotificationType(NotificationType notificationType) {
        NotificationTypeDTO notificationTypeDTO = new NotificationTypeDTO();

        notificationTypeDTO.setId(notificationType.getId());
        notificationTypeDTO.setName(notificationType.getName());
        notificationTypeDTO.setDescription(notificationType.getDescription());
        notificationTypeDTO.setType(notificationType.getType());

        return notificationTypeDTO;
    }

}
