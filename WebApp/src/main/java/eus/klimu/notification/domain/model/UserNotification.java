package eus.klimu.notification.domain.model;

import eus.klimu.channel.domain.model.Channel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement
@Entity(name = "user_notification")
public class UserNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "channel_id")
    private Channel channel;
    @ManyToMany
    private Collection<LocalizedNotification> notifications = new ArrayList<>();

}
