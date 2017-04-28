package zaawjava.DTO;

public class CountryDTO {
    private int id;
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

    public CountryDTO(int id, String countryName) {
        super();
        this.id = id;
        this.countryName = countryName;
    }

    public CountryDTO() {
        super();
    }

    @Override
    public String toString() {
        return "Country [id=" + id + ", countryName=" + countryName + "]";
    }
}
