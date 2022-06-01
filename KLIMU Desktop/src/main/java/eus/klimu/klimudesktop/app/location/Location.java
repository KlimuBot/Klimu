package eus.klimu.klimudesktop.app.location;

import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement
public class Location implements Comparable<Location> {

    private Long id;
    private String city;
    private String country;

    @Override
    public String toString() {
        return city + ", " + country;
    }

    @Override
    public int compareTo(Location l) {
        return this.toString().toLowerCase().compareTo(l.toString().toLowerCase());
    }
}
