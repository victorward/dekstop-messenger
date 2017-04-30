package DTO;

import org.apache.commons.lang.builder.EqualsBuilder;

import java.io.Serializable;

public class LanguageDTO implements Serializable {
    private int id;
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

    public LanguageDTO(String languageName) {
        super();
        this.languageName = languageName;
    }

    public LanguageDTO() {
        super();
    }

    @Override
    public String toString() {
        return "Language [id=" + id + ", languageName=" + languageName + "]";
    }

//    @Override
//    public boolean equals(Object obj) {
////        if (!(obj instanceof LanguageDTO))
////            return false;
//        if (obj == this)
//            return true;
//
//        LanguageDTO rhs = (LanguageDTO) obj;
//        if (((LanguageDTO) obj).getLanguageName().equals(rhs.getLanguageName()))
//            return true;
//        return new EqualsBuilder().
//                append(languageName, rhs.languageName).
//                isEquals();
//    }
}
