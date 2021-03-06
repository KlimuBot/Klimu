package eus.klimu.notification.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@Getter
@Setter
@XmlRootElement
@NoArgsConstructor
public class NotificationTypeDTO implements Serializable {

    private Long id;
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
