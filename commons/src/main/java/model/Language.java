package model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="language")
public class Language implements Serializable
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private int id;

	@Column(name="language_name")
	private String languageName;

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getLanguageName()
	{
		return languageName;
	}

	public void setLanguageName(String languageName)
	{
		this.languageName = languageName;
	}

	public Language(String languageName)
	{
		super();
		this.languageName = languageName;
	}

	public Language()
	{
		super();
	}

	@Override
	public String toString()
	{
		return "Language [id=" + id + ", languageName=" + languageName + "]";
	}

}
