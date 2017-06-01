package zaawjava.commons.utils;

import java.io.Serializable;

public class Message implements Serializable {
    private String kind;
    private Object content;

    public Message(String id, Object content) {
        this.kind = id;
        this.content = content;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Message{" +
                "kind='" + kind + '\'' +
                ", content=" + (content != null ? content.toString() : null) +
                '}';
    }
}
