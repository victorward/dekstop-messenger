package zaawjava.DTO;

public class LanguageDTO {
    private int id;
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

    public LanguageDTO(String languageName)
    {
        super();
        this.languageName = languageName;
    }

    public LanguageDTO()
    {
        super();
    }

    @Override
    public String toString()
    {
        return "Language [id=" + id + ", languageName=" + languageName + "]";
    }
}
