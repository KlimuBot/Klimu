package eus.klimu.channel.api;

import eus.klimu.channel.domain.service.definition.ChannelService;
import eus.klimu.notification.domain.model.LocalizedNotification;
import eus.klimu.security.TokenManagement;
import eus.klimu.users.domain.model.AppUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/channel")
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;
    private final TokenManagement tokenManagement = new TokenManagement();

    @GetMapping("/subscription")
    public String getSubscriptionPage(Model model) {
        log.info("Fetching the subscription page");
        model.addAttribute("channelList", channelService.getAllChannels());

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
            model.addAttribute("channelList", channelService.getAllChannels());

            return "services/channel";
        } else {
            // Add elements to model.
            model.addAttribute("channelList", channelService.getAllChannels());

            return "services/suscripciones";
        }
    }

    @GetMapping("/{channel}/add")
    public String getNotificationAddingPage(@PathVariable String channel) {
        return "services/" + channel.toLowerCase();
    }
}
