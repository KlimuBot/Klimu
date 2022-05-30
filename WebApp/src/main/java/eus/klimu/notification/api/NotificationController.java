package eus.klimu.notification.api;

import eus.klimu.location.domain.model.Location;
import eus.klimu.location.domain.service.definition.LocationService;
import eus.klimu.notification.domain.model.LocalizedNotification;
import eus.klimu.notification.domain.model.NotificationType;
import eus.klimu.notification.domain.service.definition.LocalizedNotificationService;
import eus.klimu.notification.domain.service.definition.NotificationTypeService;
import eus.klimu.notification.domain.service.definition.UserNotificationService;
import eus.klimu.security.TokenManagement;
import eus.klimu.users.domain.model.AppUser;
import eus.klimu.users.domain.service.definition.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Slf4j
@Controller
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final TokenManagement tokenManagement = new TokenManagement();
    private final LocationService locationService;
    private final NotificationTypeService notificationTypeService;
    private final UserNotificationService userNotificationService;
    private final LocalizedNotificationService localizedNotificationService;

    @PostMapping(value = "/add", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public void addNotification(
            HttpServletResponse response, HttpSession session,
            @RequestParam(value = "channel") String channel,
            @RequestParam(value = "locationId") String locationId,
            @RequestParam(value = "notificationTypeId") String notificationTypeId
    ) throws IOException {
        Location location = locationService.getLocationById(Long.parseLong(locationId));
        NotificationType notificationType = notificationTypeService.getNotificationType(Long.parseLong(notificationTypeId));
        AppUser user = tokenManagement.getUserFromTokens(session);

        if (location != null && notificationType != null && user != null) {
            LocalizedNotification localizedNotification = localizedNotificationService
                    .addNewLocalizedNotification(new LocalizedNotification(null, notificationType, location));

            user.getNotifications().forEach(userNotification -> {
                if (userNotification.getChannel().getName().equalsIgnoreCase(channel)) {
                    userNotification.getNotifications().add(localizedNotification);
                    userNotificationService.updateUserNotification(userNotification);
                }
            });
        }
        response.sendRedirect("/channel/subscription/" + channel);
    }

}
