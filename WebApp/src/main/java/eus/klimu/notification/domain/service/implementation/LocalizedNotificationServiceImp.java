package eus.klimu.notification.domain.service.implementation;

import com.google.gson.Gson;
import eus.klimu.home.api.RequestMaker;
import eus.klimu.location.domain.model.Location;
import eus.klimu.location.domain.model.LocationDTO;
import eus.klimu.notification.domain.model.LocalizedNotificationDTO;
import eus.klimu.notification.domain.model.NotificationTypeDTO;
import eus.klimu.notification.domain.service.definition.LocalizedNotificationService;
import eus.klimu.notification.domain.model.LocalizedNotification;
import eus.klimu.notification.domain.model.NotificationType;
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
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class LocalizedNotificationServiceImp implements LocalizedNotificationService {

    @Getter
    private enum LocalizedNotificationURL {

        BASE("/"), LOCATION_AND_TYPE(""), GET_ALL("/all"), GET_ALL_LOCATION("/all/location"),
        GET_ALL_TYPE("/all/type"), CREATE("/create"), CREATE_ALL("/create/all"),
        UPDATE("/update"), DELETE("/delete");

        private final String name;

        LocalizedNotificationURL(String name) {
            this.name = "https://klimu.eus/RestAPI/localized-notification" + name;
        }
    }

    private final Gson gson = new Gson();
    private final HttpSession session;
    private final RequestMaker requestMaker = new RequestMaker();

    @Override
    public LocalizedNotification getLocalizedNotification(long id) {
        log.info("Fetching notification with id={}", id);
        ResponseEntity<String> response = requestMaker.doGet(
                LocalizedNotificationURL.BASE.getName() + id,
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ), null
        );
        if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
            return gson.fromJson(response.getBody(), LocalizedNotification.class);
        }
        return null;
    }

    @Override
    public LocalizedNotification getLocalizedNotification(Location location, NotificationType notificationType) {
        log.info(
                "Fetching localized notification of type={} on location={}",
                location.toString(), notificationType.getName()
        );
        JSONObject body = new JSONObject();
        body.append("location", gson.toJson(location, Location.class));
        body.append("notificationType", gson.toJson(notificationType, NotificationType.class));

        ResponseEntity<String> response = requestMaker.doGet(
                LocalizedNotificationURL.LOCATION_AND_TYPE.getName(),
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ), body.toString()
        );
        if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
            return gson.fromJson(response.getBody(), LocalizedNotification.class);
        }
        return null;
    }

    @Override
    public List<LocalizedNotification> getAllLocalizedNotifications() {
        log.info("Fetching all localized notifications");
        ResponseEntity<String> response = requestMaker.doGet(
                LocalizedNotificationURL.GET_ALL.getName(),
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ), null
        );
        return localizedNotificationToList(response);
    }

    @Override
    public List<LocalizedNotification> getAllLocalizedNotifications(Location location) {
        log.info("Fetching all localized notifications on {}", location.toString());
        ResponseEntity<String> response = requestMaker.doGet(
                LocalizedNotificationURL.GET_ALL_LOCATION.getName(),
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ), gson.toJson(LocationDTO.fromLocation(location), LocationDTO.class)
        );
        return localizedNotificationToList(response);
    }

    @Override
    public List<LocalizedNotification> getAllLocalizedNotifications(NotificationType notificationType) {
        log.info("Fetching all localized notifications of type {}", notificationType.getName());
        ResponseEntity<String> response = requestMaker.doGet(
                LocalizedNotificationURL.GET_ALL_TYPE.getName(),
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ), gson.toJson(NotificationTypeDTO.fromNotificationType(notificationType), NotificationTypeDTO.class)
        );
        return localizedNotificationToList(response);
    }

    @Override
    public LocalizedNotification addNewLocalizedNotification(LocalizedNotification localizedNotification) {
        log.info("Saving localized notification {} on the database", localizedNotification.toString());
        ResponseEntity<String> response = requestMaker.doPost(
                LocalizedNotificationURL.CREATE.getName(),
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(
                                MediaType.APPLICATION_JSON,
                                Collections.singletonList(MediaType.APPLICATION_JSON)
                        ),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ), gson.toJson(
                        LocalizedNotificationDTO.fromLocalizedNotification(localizedNotification),
                        LocalizedNotificationDTO.class
                )
        );
        if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
            return gson.fromJson(response.getBody(), LocalizedNotification.class);
        }
        return null;
    }

    @Override
    public List<LocalizedNotification> addAllLocalizedNotifications(List<LocalizedNotification> localizedNotifications) {
        log.info("Saving {} localized notifications on the database", localizedNotifications.size());
        List<LocalizedNotificationDTO> localizedNotificationDTOS = new ArrayList<>();
        localizedNotifications.forEach(localizedNotification ->
                localizedNotificationDTOS.add(LocalizedNotificationDTO.fromLocalizedNotification(localizedNotification)));
        ResponseEntity<String> response = requestMaker.doPost(
                LocalizedNotificationURL.CREATE.getName(),
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ), gson.toJson(localizedNotificationDTOS)
        );
        return localizedNotificationToList(response);
    }

    @Override
    public LocalizedNotification updateLocalizedNotification(LocalizedNotification localizedNotification) {
        log.info("Updating localized notification with id={}", localizedNotification.getId());
        ResponseEntity<String> response = requestMaker.doPut(
                LocalizedNotificationURL.CREATE.getName(),
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ), gson.toJson(
                        LocalizedNotificationDTO.fromLocalizedNotification(localizedNotification),
                        LocalizedNotificationDTO.class
                )
        );
        if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
            return gson.fromJson(response.getBody(), LocalizedNotification.class);
        }
        return null;
    }

    @Override
    public void deleteLocalizedNotification(LocalizedNotification localizedNotification) {
        log.info("Deleting notification with id={}", localizedNotification.getId());
        ResponseEntity<String> response = requestMaker.doDelete(
                LocalizedNotificationURL.CREATE.getName(),
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ), gson.toJson(
                        LocalizedNotificationDTO.fromLocalizedNotification(localizedNotification),
                        LocalizedNotificationDTO.class
                )
        );
        assert response.getStatusCode().is2xxSuccessful();
    }

    private List<LocalizedNotification> localizedNotificationToList(ResponseEntity<String> response) {
        if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
            JSONArray jsonLN = new JSONArray(response.getBody());
            List<LocalizedNotification> lnResponse = new ArrayList<>();

            jsonLN.forEach(ln -> lnResponse.add(gson.fromJson(ln.toString(), LocalizedNotification.class)));
            return lnResponse;
        }
        return Collections.emptyList();
    }
}
