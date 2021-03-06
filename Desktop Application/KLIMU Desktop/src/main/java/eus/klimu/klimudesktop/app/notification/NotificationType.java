package eus.klimu.klimudesktop.app.notification;

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
public class NotificationType implements Comparable<NotificationType> {

    private Long id;
    private String name;
    private String description;
    private String type; // info, warning, danger, etc.

    @Override
    public boolean equals(Object type) {
        NotificationType nType = (NotificationType) type;

        if (nType != null) {
            return this.getId().equals(nType.getId());
        } else {
            return false;
        }
    }

    @Override
    public int compareTo(NotificationType n) {
        return this.name.toLowerCase().compareTo(n.getName().toLowerCase());
    }
}
