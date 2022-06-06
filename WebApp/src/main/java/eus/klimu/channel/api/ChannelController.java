package eus.klimu.channel.api;

import eus.klimu.channel.domain.model.Channel;
import eus.klimu.channel.domain.service.definition.ChannelService;
import eus.klimu.location.domain.model.Location;
import eus.klimu.location.domain.service.definition.LocationService;
import eus.klimu.notification.domain.model.NotificationType;
import eus.klimu.notification.domain.service.definition.LocalizedNotificationService;
import eus.klimu.notification.domain.service.definition.NotificationTypeService;
import eus.klimu.notification.domain.service.definition.UserNotificationService;
import eus.klimu.notification.domain.model.LocalizedNotification;
import eus.klimu.security.TokenManagement;
import eus.klimu.users.domain.model.AppUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/channel")
@RequiredArgsConstructor
public class ChannelController {

    private static final String CHANNEL_LIST = "channelList";

    private final ChannelService channelService;
    private final LocationService locationService;
    private final NotificationTypeService notificationTypeService;
    private final UserNotificationService userNotificationService;
    private final LocalizedNotificationService localizedNotificationService;

    private final TokenManagement tokenManagement = new TokenManagement();

    @GetMapping("/subscription")
    public String getSubscriptionPage(Model model) {
        log.info("Fetching the subscription page");
        model.addAttribute(CHANNEL_LIST, channelService.getAllChannels());

        return "services/suscripciones";
    }

    @GetMapping("/subscription/{channel}")
    public String getChannelPage(
            @PathVariable String channel,
            Model model, HttpSession session
    ) {
        log.info("Fetching the subscription page for {}", channel);
        AppUser user = tokenManagement.getUserFromTokens(session);

        if (user != null) {
            List<LocalizedNotification> notifications = new ArrayList<>();
            user.getNotifications().forEach(userNotification -> {
                if (userNotification.getChannel().getName().equalsIgnoreCase(channel)) {
                    notifications.addAll(userNotification.getNotifications());
                }
            });
            // Add elements to model.
            model.addAttribute("channelName", channel);
            model.addAttribute("notifications", notifications);
            model.addAttribute(CHANNEL_LIST, channelService.getAllChannels());

            return "services/channel";
        } else {
            // Add elements to model.
            model.addAttribute(CHANNEL_LIST, channelService.getAllChannels());

            return "services/suscripciones";
        }
    }

    @GetMapping("/{channel}/add")
    public String getNotificationAddingPage(@PathVariable String channel, Model model) {
        log.info("Fetching the alert configuration modification page for {}", channel);

        List<Location> locationList = locationService.getAllLocations();
        List<NotificationType> notificationList = notificationTypeService.getAllNotificationTypes();

        Collections.sort(locationList);
        Collections.sort(notificationList);

        model.addAttribute("channel", channel);
        model.addAttribute("locationList", locationList);
        model.addAttribute("notificationList", notificationList);

        return "services/add_alert";
    }

    @PostMapping("/remove/{channelName}/{id}")
    public void removeLocalizedNotificationFromUser(
            @PathVariable String channelName, @PathVariable long id,
            HttpSession session, HttpServletResponse response
    ) throws IOException {
        AppUser user = tokenManagement.getUserFromTokens(session);

        if (user != null) {
            LocalizedNotification notification = localizedNotificationService.getLocalizedNotification(id);
            Channel channel = channelService.getChannel(channelName);

            if (notification != null && channel != null) {
                // Remove the notification from the user notification for that channel.
                user.getNotifications().forEach(userNotification -> {
                    if (userNotification.getChannel().getName().equals(channel.getName())) {
                        Collection<LocalizedNotification> lnList = userNotification.getNotifications();

                        lnList.removeIf(ln -> ln.getId().equals(notification.getId()));
                        userNotification.setNotifications(lnList);

                        userNotificationService.updateUserNotification(userNotification);
                    }
                });
            }
        }
        response.sendRedirect("/channel/subscription/" + channelName);
    }
}
