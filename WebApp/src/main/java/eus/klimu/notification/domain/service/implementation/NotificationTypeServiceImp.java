package eus.klimu.notification.domain.service.implementation;

import com.google.gson.Gson;
import eus.klimu.home.api.RequestMaker;
import eus.klimu.notification.domain.model.NotificationType;
import eus.klimu.notification.domain.model.NotificationTypeDTO;
import eus.klimu.notification.domain.service.definition.NotificationTypeService;
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
public class NotificationTypeServiceImp implements NotificationTypeService {

    @Getter
    private enum NotificationTypeURL {

        BASE("/"), NAME("/name/"), GET_ALL("/all"), ALL_TYPE("/all/"),
        CREATE("/create"), CREATE_ALL("/create/all"), UPDATE("/update"), DELETE("/delete");

        private final String name;

        NotificationTypeURL(String name) {
            this.name = "https://klimu.eus/RestAPI/notification-type" + name;
        }
    }

    private final HttpSession session;
    private final Gson gson = new Gson();
    private final RequestMaker requestMaker = new RequestMaker();

    @Override
    public NotificationType getNotificationType(long id) {
        log.info("Fetching notification type with id={}", id);
        ResponseEntity<String> response = requestMaker.doGet(
                NotificationTypeURL.BASE.getName() + id,
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ), null
        );
        if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
            return gson.fromJson(response.getBody(), NotificationType.class);
        }
        return null;
    }

    @Override
    public NotificationType getNotificationType(String name) {
        log.info("Fetching notification type with name={}", name);
        ResponseEntity<String> response = requestMaker.doGet(
                NotificationTypeURL.NAME.getName() + name,
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ), null
        );
        if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
            return gson.fromJson(response.getBody(), NotificationType.class);
        }
        return null;
    }

    @Override
    public List<NotificationType> getAllNotificationTypes() {
        log.info("Fetching all notification types");
        ResponseEntity<String> response = requestMaker.doGet(
                NotificationTypeURL.GET_ALL.getName(),
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ), null
        );
        return notificationTypesToList(response);
    }

    @Override
    public List<NotificationType> getAllNotificationTypes(String type) {
        log.info("Fetching all notification types with type={}", type);
        ResponseEntity<String> response = requestMaker.doGet(
                NotificationTypeURL.ALL_TYPE.getName() + type,
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ), null
        );
        return notificationTypesToList(response);
    }

    @Override
    public NotificationType addNewNotificationType(NotificationType notificationType) {
        log.info("Saving notification type {} on the database", notificationType.getName());
        ResponseEntity<String> response = requestMaker.doPost(
                NotificationTypeURL.CREATE.getName(),
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ), gson.toJson(NotificationTypeDTO.fromNotificationType(notificationType), NotificationTypeDTO.class)
        );
        if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
            return gson.fromJson(response.getBody(), NotificationType.class);
        }
        return null;
    }

    @Override
    public List<NotificationType> addAllNotificationTypes(List<NotificationType> notificationTypes) {
        log.info("Saving {} notification types on the database", notificationTypes.size());
        List<NotificationTypeDTO> notificationTypeDTOS = new ArrayList<>();
        notificationTypes.forEach(notificationType ->
                notificationTypeDTOS.add(NotificationTypeDTO.fromNotificationType(notificationType))
        );
        ResponseEntity<String> response = requestMaker.doPost(
                NotificationTypeURL.CREATE.getName(),
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ), gson.toJson(notificationTypeDTOS)
        );
        return notificationTypesToList(response);
    }

    @Override
    public NotificationType updateNotificationType(NotificationType notificationType) {
        log.info("Updating notification type with id={}", notificationType.getId());
        ResponseEntity<String> response = requestMaker.doPut(
                NotificationTypeURL.CREATE.getName(),
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ), gson.toJson(NotificationTypeDTO.fromNotificationType(notificationType), NotificationTypeDTO.class)
        );
        if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
            return gson.fromJson(response.getBody(), NotificationType.class);
        }
        return null;
    }

    @Override
    public void deleteNotificationType(NotificationType notificationType) {
        log.info("Deleting notification type with id={}", notificationType.getId());
        ResponseEntity<String> response = requestMaker.doDelete(
                NotificationTypeURL.CREATE.getName(),
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ), gson.toJson(NotificationTypeDTO.fromNotificationType(notificationType),  NotificationTypeDTO.class)
        );
        assert response.getStatusCode().is2xxSuccessful();
    }

    private List<NotificationType> notificationTypesToList(ResponseEntity<String> response) {
        if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
            JSONArray jsonNT = new JSONArray(response.getBody());
            List<NotificationType> ntResponse = new ArrayList<>();

            jsonNT.forEach(nt -> ntResponse.add(gson.fromJson(nt.toString(), NotificationType.class)));
            return ntResponse;
        }
        return Collections.emptyList();
    }
}
