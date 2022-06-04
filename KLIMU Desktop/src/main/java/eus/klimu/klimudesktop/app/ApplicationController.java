package eus.klimu.klimudesktop.app;

import eus.klimu.klimudesktop.app.notification.Notification;
import eus.klimu.klimudesktop.app.user.AppUser;
import eus.klimu.klimudesktop.connection.RabbitConnector;
import eus.klimu.klimudesktop.security.TokenManagement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/klimu")
@RequiredArgsConstructor
public class ApplicationController {

    private final TokenManagement tokenManagement = new TokenManagement();
    private final RequestManagement requestManagement;
    private RabbitConnector rabbitConnector = null;

    @GetMapping("/app")
    public String getMainPage(HttpSession session, Model model) {
        AppUser user = tokenManagement.getUserFromTokens(session);

        if (user != null) {
            model.addAttribute("notifications", requestManagement.getUserNotifications(user));
            if (rabbitConnector == null) {
                rabbitConnector = new RabbitConnector(user.getUsername());
                rabbitConnector.start();
            }
        } else {
            model.addAttribute("notifications", new ArrayList<>());
        }
        return "index";
    }

    @GetMapping("/notification")
    public ResponseEntity<List<Notification>> getPendingNotifications() {
        List<Notification> notifications = new ArrayList<>();

        if (rabbitConnector != null && rabbitConnector.isAlive()) {
            notifications = rabbitConnector.getNotifications();
        }
        return ResponseEntity.ok().body(notifications);
    }

    @GetMapping(value = "/login")
    public String getLoginPage() {
        return "login";
    }

}
