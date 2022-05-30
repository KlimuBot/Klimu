package eus.klimu.notification.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LocalizedNotificationDTO {

    private Long id;
    private Long notificationTypeId;
    private Long locationId;

    public static LocalizedNotificationDTO fromLocalizedNotification(LocalizedNotification localizedNotification) {
        LocalizedNotificationDTO localizedNotificationDTO = new LocalizedNotificationDTO();

        localizedNotificationDTO.setId(localizedNotification.getId());
        localizedNotificationDTO.setLocationId(localizedNotification.getLocation().getId());
        localizedNotificationDTO.setNotificationTypeId(localizedNotification.getType().getId());

        return localizedNotificationDTO;
    }

}
