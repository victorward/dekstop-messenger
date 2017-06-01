package zaawjava.server.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "languages")
public class Language implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "language_name")
    private String languageName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public Language(String languageName) {
        super();
        this.languageName = languageName;
    }

    public Language() {
        super();
    }

    @Override
    public String toString() {
        return "Language [id=" + id + ", languageName=" + languageName + "]";
    }

}
