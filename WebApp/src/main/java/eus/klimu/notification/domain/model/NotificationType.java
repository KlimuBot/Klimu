package eus.klimu.notification.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "notification_type")
public class NotificationType implements Comparable<NotificationType> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(unique = true)
    private String name;
    private String description;
    private String type; // info, warning, danger, etc.

    @Override
    public int compareTo(NotificationType n) {
        return this.name.toLowerCase().compareTo(n.getName().toLowerCase());
    }
}
