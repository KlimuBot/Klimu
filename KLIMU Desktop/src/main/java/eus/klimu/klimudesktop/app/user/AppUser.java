package eus.klimu.klimudesktop.app.user;

import eus.klimu.klimudesktop.app.notification.UserNotification;
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
public class AppUser {

    private Long id;
    private String username;
    private String password;
    private String name;
    private String surname;
    private String email;
    private String number;
    private String telegramId;
    private Collection<Role> roles = new ArrayList<>();
    private Collection<UserNotification> notifications = new ArrayList<>();

}