package eus.klimu.location.domain.service.implementation;

import com.google.gson.Gson;
import eus.klimu.home.api.RequestMaker;
import eus.klimu.location.domain.model.Location;
import eus.klimu.location.domain.model.LocationDTO;
import eus.klimu.location.domain.service.definition.LocationService;
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
public class LocationServiceImp implements LocationService {

    @Getter
    private enum LocationURL {

        BASE("/"), CITY("/city/"), COUNTRY("/country/"), GET_ALL("/all"),
        CREATE("/create"), CREATE_ALL("/create/all"), UPDATE("/update"),
        DELETE("/delete"), DELETE_CITY("/delete/city/"), DELETE_COUNTRY("/delete/country/");

        private final String name;

        LocationURL(String name) {
            this.name = "https://klimu.eus/RestAPI/location" + name;
        }
    }

    private final HttpSession session;
    private final Gson gson = new Gson();
    private final RequestMaker requestMaker = new RequestMaker();

    @Override
    public Location getLocationById(long id) {
        log.info("Fetching location with id={}", id);
        ResponseEntity<String> response = requestMaker.doGet(
                LocationURL.BASE.getName() + id,
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ), null
        );
        if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
            return gson.fromJson(response.getBody(), Location.class);
        }
        return null;
    }

    @Override
    public List<Location> getAllLocations() {
        log.info("Fetching all locations");
        ResponseEntity<String> response = requestMaker.doGet(
                LocationURL.GET_ALL.getName(),
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ), null
        );
        return locationsToList(response);
    }

    @Override
    public Location getLocationByCity(String city) {
        log.info("Fetching city with name={}", city);
        ResponseEntity<String> response = requestMaker.doGet(
                LocationURL.CITY.getName() + city,
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ), null
        );
        if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
            return gson.fromJson(response.getBody(), Location.class);
        }
        return null;
    }

    @Override
    public Location getLocationByCountry(String country) {
        log.info("Fetching country with name={}", country);
        ResponseEntity<String> response = requestMaker.doGet(
                LocationURL.COUNTRY.getName() + country,
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ), null
        );
        if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
            return gson.fromJson(response.getBody(), Location.class);
        }
        return null;
    }

    @Override
    public Location getLocationByCityAndCountry(String city, String country) {
        log.info("Fetching city with name={} and country with name={}", city, country);
        ResponseEntity<String> response = requestMaker.doGet(
                LocationURL.BASE.getName() + city + "/" + country,
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ), null
        );
        if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
            return gson.fromJson(response.getBody(), Location.class);
        }
        return null;
    }

    @Override
    public Location addNewLocation(Location location) {
        log.info("Saving location ({}) on the database", location.toString());
        ResponseEntity<String> response = requestMaker.doPost(
                LocationURL.CREATE_ALL.getName(),
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ), gson.toJson(LocationDTO.fromLocation(location), LocationDTO.class)
        );
        if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
            return gson.fromJson(response.getBody(), Location.class);
        }
        return null;
    }

    @Override
    public List<Location> addAllLocations(List<Location> locations) {
        log.info("Saving {} location(s)", locations.size());
        List<LocationDTO> locationDTOS = new ArrayList<>();
        locations.forEach(location ->
                locationDTOS.add(LocationDTO.fromLocation(location))
        );
        ResponseEntity<String> response = requestMaker.doPost(
                LocationURL.CREATE_ALL.getName(),
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ), gson.toJson(locationDTOS)
        );
        return locationsToList(response);
    }

    @Override
    public Location updateLocation(Location location) {
        log.info("Updating location with id={}", location.getId());
        ResponseEntity<String> response = requestMaker.doPut(
                LocationURL.CREATE_ALL.getName(),
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ), gson.toJson(LocationDTO.fromLocation(location), LocationDTO.class)
        );
        if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
            return gson.fromJson(response.getBody(), Location.class);
        }
        return null;
    }

    @Override
    public void deleteLocation(Location location) {
        log.info("Deleting location with id={}", location.getId());
        ResponseEntity<String> response = requestMaker.doDelete(
                LocationURL.CREATE_ALL.getName(),
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ), gson.toJson(LocationDTO.fromLocation(location), LocationDTO.class)
        );
        assert response.getStatusCode().is2xxSuccessful();
    }

    private List<Location> locationsToList(ResponseEntity<String> response) {
        if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
            JSONArray jsonLocations = new JSONArray(response.getBody());
            List<Location> locationResponse = new ArrayList<>();

            jsonLocations.forEach(location -> locationResponse.add(gson.fromJson(location.toString(), Location.class)));
            return locationResponse;
        }
        return Collections.emptyList();
    }
}
