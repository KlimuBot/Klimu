package eus.klimu.notification.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LocalizedNotificationDTO {

    private long id;
    private long notificationTypeId;
    private long locationId;

}
