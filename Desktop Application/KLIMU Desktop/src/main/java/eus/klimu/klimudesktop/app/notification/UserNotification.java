package eus.klimu.klimudesktop.app.notification;

import eus.klimu.klimudesktop.app.channel.Channel;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement
public class UserNotification {

    private Long id;
    private Channel channel;
    private Collection<LocalizedNotification> notifications = new ArrayList<>();

}
