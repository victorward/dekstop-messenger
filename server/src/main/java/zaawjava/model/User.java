package zaawjava.model;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User implements Serializable {


    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "users_languages",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "language_id")})
    private Set<Language> languages = new HashSet<Language>(0);

    public Set<Language> getLanguages() {
        return languages;
    }

    public void setLanguages(Set<Language> languages) {
        this.languages = languages;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "phone")
    private int phone;

    @Column(name = "gender")
    private String gender;

    @Column(name = "email")
    private String email;

    @Column(name = "user_password")
    private String password;

    @Column(name = "address")
    private String address;

    @ManyToOne(cascade = {CascadeType.ALL})
    private Country country;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "photo")
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

    public User() {
        super();
    }

    public User(String email, String password) {
        super();
        this.email = email;
        this.password = password;
    }

    public User(String email, String password, String firstName, String lastName, LocalDate birthDate, String gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.gender = gender;
        this.email = email;
        this.password = password;
    }

    public User(int id, Set<Language> languages, String firstName, String lastName, int phone, String gender,
                String email, String password, String address, Country country, LocalDate birthDate, String photo) {
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

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

}
