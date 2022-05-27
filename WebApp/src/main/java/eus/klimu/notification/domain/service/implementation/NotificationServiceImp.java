package eus.klimu.notification.domain.service.implementation;

import com.google.gson.Gson;
import eus.klimu.home.api.RequestMaker;
import eus.klimu.location.domain.model.Location;
import eus.klimu.notification.domain.model.DatePeriod;
import eus.klimu.notification.domain.model.Notification;
import eus.klimu.notification.domain.model.NotificationType;
import eus.klimu.notification.domain.service.definition.NotificationService;
import eus.klimu.security.TokenManagement;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NotificationServiceImp implements NotificationService {

    @Getter
    private enum NotificationURL {

        BASE("/"), LOCATION("/location"), TYPE("/type"),
        DATE_LOCATION("/date/location"), DATE_TYPE("/date/type"), GET_ALL("/all"),
        CREATE("/create"), CREATE_ALL("/create/all"), UPDATE("/update"), DELETE("/delete");

        private final String name;

        NotificationURL(String name) {
            this.name = "https://klimu.eus/RestAPI/notification" + name;
        }
    }

    private final HttpSession session;
    private final Gson gson = new Gson();
    private final RequestMaker requestMaker = new RequestMaker();

    @Override
    public Notification getNotificationById(long id) {
        log.info("Fetching notification with id={}", id);
        ResponseEntity<String> response = requestMaker.doGet(
                NotificationURL.BASE.getName() + id,
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ), null
        );
        if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
            return gson.fromJson(response.getBody(), Notification.class);
        }
        return null;
    }

    @Override
    public List<Notification> getAllNotifications(Location location) {
        log.info("Fetching all notifications for location={}", location);
        ResponseEntity<String> response = requestMaker.doGet(
                NotificationURL.LOCATION.getName(),
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ), gson.toJson(location, Location.class)
        );
        return notificationsToList(response);
    }

    @Override
    public List<Notification> getAllNotifications(NotificationType type) {
        log.info("Fetching all notifications of type={}", type);
        ResponseEntity<String> response = requestMaker.doGet(
                NotificationURL.TYPE.getName(),
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ), gson.toJson(type, NotificationType.class)
        );
        return notificationsToList(response);
    }

    @Override
    public List<Notification> getAllNotifications(Date startDate, Date endDate) {
        log.info("Fetching all notifications between {} and {}", startDate, endDate);
        ResponseEntity<String> response = requestMaker.doGet(
                NotificationURL.GET_ALL.getName(),
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ), gson.toJson(new DatePeriod(startDate, endDate), DatePeriod.class)
        );
        return notificationsToList(response);
    }

    @Override
    public List<Notification> getNotificationsByDateBetween(Location location, Date startDate, Date endDate) {
        log.info("Fetching all notifications for location={} between {} and {}", location, startDate, endDate);
        JSONObject body = new JSONObject();
        body.append("location", location);
        body.append("datePeriod", new DatePeriod(startDate, endDate));

        ResponseEntity<String> response = requestMaker.doGet(
                NotificationURL.DATE_LOCATION.getName(),
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ), body.toString()
        );
        return notificationsToList(response);
    }

    @Override
    public List<Notification> getNotificationsByDateBetween(NotificationType type, Date startDate, Date endDate) {
        log.info("Fetching all notifications of type={} between {} and {}", type, startDate, endDate);
        JSONObject body = new JSONObject();
        body.append("notificationType", type);
        body.append("datePeriod", new DatePeriod(startDate, endDate));

        ResponseEntity<String> response = requestMaker.doGet(
                NotificationURL.DATE_TYPE.getName(),
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ), body.toString()
        );
        return notificationsToList(response);
    }

    @Override
    public Notification addNewNotification(Notification notification) {
        log.info(
                "Saving notification {} on the database", notification.getType() +
                " (" + notification.getLocation() + ")[" + notification.getDate() + "]"
        );
        ResponseEntity<String> response = requestMaker.doPost(
                NotificationURL.CREATE.getName(),
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ), gson.toJson(notification, Notification.class)
        );
        if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
            return gson.fromJson(response.getBody(), Notification.class);
        }
        return null;
    }

    @Override
    public List<Notification> addAllNotifications(List<Notification> notifications) {
        log.info("Saving {} new notifications", notifications.size());
        ResponseEntity<String> response = requestMaker.doPost(
                NotificationURL.CREATE.getName(),
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ), gson.toJson(notifications)
        );
        return notificationsToList(response);
    }

    @Override
    public Notification updateNotification(Notification notification) {
        log.info("Updating notification with id={}", notification.getId());
        ResponseEntity<String> response = requestMaker.doPut(
                NotificationURL.CREATE.getName(),
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ), gson.toJson(notification, Notification.class)
        );
        if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
            return gson.fromJson(response.getBody(), Notification.class);
        }
        return null;
    }

    @Override
    public void deleteNotification(Notification notification) {
        log.info("Deleting notification with id={}", notification.getId());
        ResponseEntity<String> response = requestMaker.doDelete(
                NotificationURL.CREATE.getName(),
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ), gson.toJson(notification, Notification.class)
        );
        assert response.getStatusCode().is2xxSuccessful();
    }

    private List<Notification> notificationsToList(ResponseEntity<String> response) {
        if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
            JSONArray jsonNotification = new JSONArray(response.getBody());
            List<Notification> notificationResponse = new ArrayList<>();

            jsonNotification.forEach(notification ->
                    notificationResponse.add(gson.fromJson(notification.toString(), Notification.class))
            );
            return notificationResponse;
        }
        return Collections.emptyList();
    }
}
