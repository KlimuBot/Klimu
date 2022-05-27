package eus.klimu.location.domain.service.definition;

import eus.klimu.location.domain.model.Location;

import java.util.List;

public interface LocationService {

    List<Location> getAllLocations();
    Location getLocationById(long id);
    Location getLocationByCity(String city);
    Location getLocationByCountry(String country);
    Location getLocationByCityAndCountry(String city, String country);
    Location addNewLocation(Location location);
    List<Location> addAllLocations(List<Location> locations);
    Location updateLocation(Location location);
    void deleteLocation(Location location);

}
