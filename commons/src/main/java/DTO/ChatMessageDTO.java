package DTO;

import java.io.Serializable;
import java.util.Date;

public class ChatMessageDTO implements Serializable {
    private int id;
    private UserDTO sender;
    private UserDTO recipient;
    private String content;
    private Date date;


    public ChatMessageDTO() {
    }

    public ChatMessageDTO(UserDTO sender, String content) {
        this.sender = sender;
        this.content = content;
    }

    public ChatMessageDTO(UserDTO sender, UserDTO recipient, String content) {
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserDTO getSender() {
        return sender;
    }

    public void setSender(UserDTO sender) {
        this.sender = sender;
    }

    public UserDTO getRecipient() {
        return recipient;
    }

    public void setRecipient(UserDTO recipient) {
        this.recipient = recipient;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "ChatMessageDTO{" +
                "id=" + id +
                ", sender=" + sender +
                ", recipient=" + recipient +
                ", content='" + content + '\'' +
                ", date=" + date +
                '}';
    }
}
