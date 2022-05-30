package eus.klimu.users.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AppUserDTO implements Serializable {

    private Long id;
    private String username;
    private String password;
    private String name;
    private String surname;
    private String email;
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

        List<Long> roles = new ArrayList<>();
        appUser.getRoles().forEach(role -> roles.add(role.getId()));
        appUserDTO.setRoles(roles);

        List<Long> notifications = new ArrayList<>();
        appUser.getNotifications().forEach(userNotification -> notifications.add(userNotification.getId()));
        appUserDTO.setNotifications(notifications);

        return appUserDTO;
    }

}
