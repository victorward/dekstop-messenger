package zaawjava.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "countries")
public class Country implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "country_name")
    private String countryName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public Country(int id, String countryName) {
        super();
        this.id = id;
        this.countryName = countryName;
    }

    public Country() {
        super();
    }

    @Override
    public String toString() {
        return "Country [id=" + id + ", countryName=" + countryName + "]";
    }

}
