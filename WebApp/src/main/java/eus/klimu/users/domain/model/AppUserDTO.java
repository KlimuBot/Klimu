package eus.klimu.users.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@XmlRootElement
@NoArgsConstructor
public class AppUserDTO implements Serializable {

    private Long id;
    private String username;
    private String password;
    private String name;
    private String surname;
    private String email;
    private String number;
    private String telegramId;
    private Collection<Long> roles;
    private Collection<Long> notifications;

    public static AppUserDTO fromAppUser(AppUser appUser) {
        AppUserDTO appUserDTO = new AppUserDTO();

        appUserDTO.setId(appUser.getId());
        appUserDTO.setUsername(appUser.getUsername());
        appUserDTO.setPassword(appUser.getPassword());
        appUserDTO.setName(appUser.getName());
        appUserDTO.setSurname(appUser.getSurname());
        appUserDTO.setEmail(appUser.getEmail());
        appUserDTO.setNumber(appUser.getNumber());
        appUserDTO.setTelegramId(appUser.getTelegramId());

        List<Long> roles = new ArrayList<>();
        appUser.getRoles().forEach(role -> roles.add(role.getId()));
        appUserDTO.setRoles(roles);

        List<Long> notifications = new ArrayList<>();
        appUser.getNotifications().forEach(userNotification -> notifications.add(userNotification.getId()));
        appUserDTO.setNotifications(notifications);

        return appUserDTO;
    }

}
