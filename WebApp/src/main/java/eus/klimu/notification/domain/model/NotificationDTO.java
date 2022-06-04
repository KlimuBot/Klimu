package eus.klimu.notification.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@Getter
@Setter
@XmlRootElement
@NoArgsConstructor
public class NotificationDTO implements Serializable {

    private Long id;
    private String message;
    private String date;
    private Long notificationTypeId;
    private Long locationId;

    public static NotificationDTO fromNotification(Notification notification) {
        NotificationDTO notificationDTO = new NotificationDTO();
        Calendar calendar = new GregorianCalendar();

        calendar.setTime(notification.getDate());

        notificationDTO.setId(notification.getId());
        notificationDTO.setMessage(notification.getMessage());
        notificationDTO.setDate(
                calendar.get(Calendar.YEAR) + "-" +
                calendar.get(Calendar.MONTH) + "-" +
                calendar.get(Calendar.DAY_OF_MONTH) + "," +
                calendar.get(Calendar.HOUR) + ":" +
                calendar.get(Calendar.MINUTE) + ":" +
                calendar.get(Calendar.SECOND)
        );
        notificationDTO.setNotificationTypeId(notification.getType().getId());
        notificationDTO.setLocationId(notification.getLocation().getId());

        return notificationDTO;
    }

}
