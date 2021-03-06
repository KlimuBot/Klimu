package eus.klimu.location.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@Getter
@Setter
@XmlRootElement
@NoArgsConstructor
public class LocationDTO implements Serializable {

    private long id;
    private String city;
    private String country;

    public static LocationDTO fromLocation(Location location) {
        LocationDTO locationDTO = new LocationDTO();

        locationDTO.setId(location.getId());
        locationDTO.setCity(location.getCity());
        locationDTO.setCountry(location.getCountry());

        return locationDTO;
    }

}
