package eus.klimu.location.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlRootElement;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement
@Entity(name = "location")
public class Location implements Comparable<Location> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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
