package eus.klimu.notification.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class NotificationDTO implements Serializable {

    private long id;
    private String message;
    private Date date;
    private long notificationTypeId;
    private long locationId;

}
