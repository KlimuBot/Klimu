package eus.klimu.users.api;

import eus.klimu.channel.domain.model.Channel;
import eus.klimu.channel.domain.service.definition.ChannelService;
import eus.klimu.notification.domain.model.UserNotification;
import eus.klimu.notification.domain.service.definition.UserNotificationService;
import eus.klimu.users.domain.model.AppUser;
import eus.klimu.users.domain.service.definition.RoleService;
import eus.klimu.users.domain.service.definition.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RoleService roleService;
    private final ChannelService channelService;
    private final UserNotificationService userNotificationService;

    @GetMapping("/{username}")
    public ResponseEntity<AppUser> getUser(@PathVariable String username) {
        return ResponseEntity.ok().body(userService.getUser(username));
    }

    @GetMapping("/create")
    public String getCreateUser() {
        return "users/create_user";
    }

    @PostMapping("/create")
    public void saveUser(
            HttpServletResponse response,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "surname") String surname,
            @RequestParam(value = "number") String number,
            @RequestParam(value = "email") String email,
            @RequestParam(value = "username") String username,
            @RequestParam(value = "password") String password
    ) throws IOException {
        if (name != null && surname != null && number != null && email != null && username != null && password != null) {
            List<Channel> channelList = channelService.getAllChannels();

            if (channelList != null) {
                List<UserNotification> userNotifications = new ArrayList<>();
                channelList.forEach(channel -> {
                    UserNotification userNotification = userNotificationService.addNewUserNotification(
                            new UserNotification(null, channel, new ArrayList<>())
                    );
                    userNotifications.add(userNotification);
                });
                AppUser user = userService.saveUser(new AppUser(
                        null, username, password, name, surname, email, number,
                        Collections.singletonList(roleService.getRole("USER_ROLE")),
                        userNotifications
                ));
                if (user != null && user.getId() != null) {
                    response.sendRedirect("/channel/subscription");
                } else {
                    response.sendRedirect("/");
                }
            } else {
                response.sendRedirect("/");
            }
        } else {
            response.sendRedirect("/user/create");
        }
    }

}
