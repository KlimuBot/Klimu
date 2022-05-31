package eus.klimu.notification.domain.service.implementation;

import com.google.gson.Gson;
import eus.klimu.channel.domain.model.Channel;
import eus.klimu.channel.domain.model.ChannelDTO;
import eus.klimu.home.api.RequestMaker;
import eus.klimu.notification.domain.model.LocalizedNotification;
import eus.klimu.notification.domain.model.LocalizedNotificationDTO;
import eus.klimu.notification.domain.model.UserNotification;
import eus.klimu.notification.domain.model.UserNotificationDTO;
import eus.klimu.notification.domain.service.definition.UserNotificationService;
import eus.klimu.security.TokenManagement;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserNotificationServiceImp implements UserNotificationService {

    @Getter
    private enum UserNotificationURL {

        BASE("/"), GET_ALL("/all"), ALL_CHANNEL("/all/channel"), ALL_NOTIFICATION("/all/notification"),
        CREATE("/create"), CREATE_ALL("/create/all"), UPDATE("/update"), DELETE("/delete");

        private final String name;

        UserNotificationURL(String name) {
            this.name = "https://klimu.eus/RestAPI/user-notification" + name;
        }
    }

    private final HttpSession session;
    private final Gson gson = new Gson();
    private final RequestMaker requestMaker = new RequestMaker();

    @Override
    public UserNotification getUserNotification(long id) {
        log.info("Fetching user notification with id={}", id);
        ResponseEntity<String> response = requestMaker.doGet(
                UserNotificationURL.BASE.getName() + id,
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ), null
        );
        if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
            return gson.fromJson(response.getBody(), UserNotification.class);
        }
        return null;
    }

    @Override
    public List<UserNotification> getUserNotifications() {
        log.info("Fetching all user notifications from database");
        ResponseEntity<String> response = requestMaker.doGet(
                UserNotificationURL.GET_ALL.getName(),
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ), null
        );
        return userNotificationsToList(response);
    }

    @Override
    public List<UserNotification> getUserNotificationsByChannel(Channel channel) {
        log.info("Fetching user notifications with channel={}", channel.getName());
        ResponseEntity<String> response = requestMaker.doGet(
                UserNotificationURL.ALL_CHANNEL.getName(),
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ), gson.toJson(ChannelDTO.fromChannel(channel), ChannelDTO.class)
        );
        return userNotificationsToList(response);
    }

    @Override
    public List<UserNotification> getUserNotificationsByNotification(LocalizedNotification localizedNotification) {
        log.info("Fetching user notifications with localized notification for {}", localizedNotification.toString());
        ResponseEntity<String> response = requestMaker.doGet(
                UserNotificationURL.ALL_CHANNEL.getName(),
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ), gson.toJson(
                        LocalizedNotificationDTO.fromLocalizedNotification(localizedNotification),
                        LocalizedNotificationDTO.class
                )
        );
        return userNotificationsToList(response);
    }

    @Override
    public UserNotification addNewUserNotification(UserNotification userNotification) {
        log.info("Saving a user notification on the database");
        ResponseEntity<String> response = requestMaker.doPost(
                UserNotificationURL.CREATE.getName(),
                requestMaker.generateHeaders(MediaType.APPLICATION_JSON, Collections.singletonList(MediaType.APPLICATION_JSON)),
                gson.toJson(
                        UserNotificationDTO.fromUserNotification(userNotification),
                        UserNotificationDTO.class
                )
        );
        if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
            return gson.fromJson(response.getBody(), UserNotification.class);
        }
        return null;
    }

    @Override
    public List<UserNotification> addAllUserNotifications(List<UserNotification> userNotifications) {
        log.info("Saving {} user notifications on the database", userNotifications.size());
        List<UserNotificationDTO> userNotificationDTOS = new ArrayList<>();
        userNotifications.forEach(userNotification ->
                userNotificationDTOS.add(UserNotificationDTO.fromUserNotification(userNotification))
        );
        ResponseEntity<String> response = requestMaker.doPost(
                UserNotificationURL.CREATE.getName(),
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ), gson.toJson(userNotificationDTOS)
        );
        return userNotificationsToList(response);
    }

    @Override
    public UserNotification updateUserNotification(UserNotification userNotification) {
        log.info("Updating user notification with id={}", userNotification);
        ResponseEntity<String> response = requestMaker.doPut(
                UserNotificationURL.UPDATE.getName(),
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(
                                MediaType.APPLICATION_JSON,
                                Collections.singletonList(MediaType.APPLICATION_JSON)
                        ),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ),
                gson.toJson(
                        UserNotificationDTO.fromUserNotification(userNotification),
                        UserNotificationDTO.class
                )
        );
        if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
            return gson.fromJson(response.getBody(), UserNotification.class);
        }
        return null;
    }

    @Override
    public void deleteUserNotifications(UserNotification userNotification) {
        log.info("Deleting user notification with id={}", userNotification.getId());
        ResponseEntity<String> response = requestMaker.doDelete(
                UserNotificationURL.DELETE.getName(),
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ), gson.toJson(UserNotificationDTO.fromUserNotification(userNotification), UserNotificationDTO.class)
        );
        assert response.getStatusCode().is2xxSuccessful();
    }

    private List<UserNotification> userNotificationsToList(ResponseEntity<String> response) {
        if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
            JSONArray jsonUN = new JSONArray(response.getBody());
            List<UserNotification> unResponse = new ArrayList<>();

            jsonUN.forEach(un -> unResponse.add(gson.fromJson(un.toString(), UserNotification.class)));
            return unResponse;
        }
        return Collections.emptyList();
    }
}
