package eus.klimu.notification.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@XmlRootElement
@NoArgsConstructor
public class NotificationDTO implements Serializable {

    private Long id;
    private String message;
    private Date date;
    private Long notificationTypeId;
    private Long locationId;

    public static NotificationDTO fromNotification(Notification notification) {
        NotificationDTO notificationDTO = new NotificationDTO();

        notificationDTO.setId(notification.getId());
        notificationDTO.setMessage(notification.getMessage());
        notificationDTO.setDate(notification.getDate());
        notificationDTO.setNotificationTypeId(notification.getType().getId());
        notificationDTO.setLocationId(notification.getLocation().getId());

        return notificationDTO;
    }

}
