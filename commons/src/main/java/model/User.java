package model;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="user")
public class User implements Serializable
{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private int id;

	@Column(name="first_name")
    private String firstName;

	@Column(name="last_name")
    private String lastName;

	@Column(name="phone")
	private int phone;

	@Column(name="gender")
	private String gender;

	@Column(name="email")
	private String email;

	@Column(name="user_password")
    private String password;

	@Column(name="address")
	private String address;

	@Column(name="country_id")
	private int countryId;

	@Column(name="birth_date")
	private Date birthDate;

	@Column(name="photo")
	private String photo;

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	public int getPhone()
	{
		return phone;
	}

	public void setPhone(int phone)
	{
		this.phone = phone;
	}

	public String getGender()
	{
		return gender;
	}

	public void setGender(String gender)
	{
		this.gender = gender;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}

	public int getCountryId()
	{
		return countryId;
	}

	public void setCountryId(int countryId)
	{
		this.countryId = countryId;
	}

	public Date getBirthDate()
	{
		return birthDate;
	}

	public void setBirthDate(Date birthDate)
	{
		this.birthDate = birthDate;
	}

	public String getPhoto()
	{
		return photo;
	}

	public void setPhoto(String photo)
	{
		this.photo = photo;
	}

	public User()
	{
		super();
	}

	public User(String email, String password)
	{
		super();
		this.email = email;
		this.password = password;
	}

	@Override
	public String toString()
	{
		return "User [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", phone=" + phone
				+ ", gender=" + gender + ", email=" + email + ", password=" + password + ", address=" + address
				+ ", countryId=" + countryId + ", birthDate=" + birthDate + ", photo=" + photo + "]";
	}

}
