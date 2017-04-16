package model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="country")
public class Country implements Serializable
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private int id;

	@Column(name="country_name")
	private String countryName;

	public int getId()
	{
		return id;
	}
	public void setId(int id)
	{
		this.id = id;
	}
	public String getCountryName()
	{
		return countryName;
	}
	public void setCountryName(String countryName)
	{
		this.countryName = countryName;
	}
	public Country(String countryName)
	{
		super();
		this.countryName = countryName;
	}
	public Country()
	{
		super();
	}
	@Override
	public String toString()
	{
		return "Country [id=" + id + ", countryName=" + countryName + "]";
	}

}
