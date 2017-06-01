package zaawjava.commons.DTO;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class UserDTO implements Serializable {
    private Set<LanguageDTO> languages = new HashSet<LanguageDTO>(0);

    public Set<LanguageDTO> getLanguages() {
        return languages;
    }

    public void setLanguages(Set<LanguageDTO> languages) {
        this.languages = languages;
    }

    private int id;

    private String firstName;

    private String lastName;

    private int phone;

    private String gender;

    private String email;

    private String password;

    private String address;

    private CountryDTO country;

    private LocalDate birthDate;

    private String photo;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getPhone() {
        return phone;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public CountryDTO getCountry() {
        return country;
    }

    public void setCountry(CountryDTO country) {
        this.country = country;
    }

    public UserDTO() {
        super();
    }

    public UserDTO(String email, String password) {
        super();
        this.email = email;
        this.password = password;
    }

    public UserDTO(String email, String password, String firstName, String lastName, LocalDate birthDate, String gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.gender = gender;
        this.email = email;
        this.password = password;
    }

    public UserDTO(int id, String email, String password, String firstName, String lastName, LocalDate birthDate, String gender) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.gender = gender;
        this.email = email;
        this.password = password;
    }

    public UserDTO(int id, String email, String password, String firstName, String lastName, LocalDate birthDate, String gender, String photo) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.gender = gender;
        this.email = email;
        this.password = password;
        this.photo = photo;
    }

    public UserDTO(String email, String password, String firstName, String lastName, LocalDate birthDate, String gender, String photo) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.gender = gender;
        this.email = email;
        this.password = password;
        this.photo = photo;
    }


    public UserDTO(Set<LanguageDTO> languages, String firstName, String lastName, int phone, String gender,
                   String email, String password, String address, CountryDTO country, LocalDate birthDate, String photo) {
        super();
        this.languages = languages;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.gender = gender;
        this.email = email;
        this.password = password;
        this.address = address;
        this.country = country;
        this.birthDate = birthDate;
        this.photo = photo;
    }

    public UserDTO(int id, Set<LanguageDTO> languages, String firstName, String lastName, int phone, String gender,
                   String email, String password, String address, CountryDTO country, LocalDate birthDate, String photo) {
        super();
        this.id = id;
        this.languages = languages;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.gender = gender;
        this.email = email;
        this.password = password;
        this.address = address;
        this.country = country;
        this.birthDate = birthDate;
        this.photo = photo;
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", phone=" + phone
                + ", gender=" + gender + ", email=" + email + ", password=" + password + ", address=" + address
                + ", birthDate=" + birthDate + ", photo=" + photo + "]";
    }

}

