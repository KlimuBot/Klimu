package eus.klimu.klimudesktop.app.notification;

import eus.klimu.klimudesktop.app.location.Location;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement
public class LocalizedNotification {

    private Long id;
    private NotificationType type;
    private Location location;

    @Override
    public String toString() {
        return type.getName() + " [" + location.toString() + "]";
    }
}
