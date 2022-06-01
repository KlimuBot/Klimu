package eus.klimu.klimudesktop.app.notification;

import eus.klimu.klimudesktop.app.location.Location;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    private Long id;
    private String message;
    private Date date;
    private NotificationType type;
    private Location location;

}
