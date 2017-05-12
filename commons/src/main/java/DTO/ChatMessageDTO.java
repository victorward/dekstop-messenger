package DTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ChatMessageDTO implements Serializable {
    private UserDTO sender;
    private List<UserDTO> recipients;
    private String content;

    public ChatMessageDTO() {
    }

    public ChatMessageDTO(UserDTO sender, String content) {
        this.sender = sender;
        this.content = content;
    }

    public ChatMessageDTO(UserDTO sender, List<UserDTO> recipients, String content) {
        if (recipients == null) this.recipients = new ArrayList<>();
        this.sender = sender;
        this.recipients = recipients;
        this.content = content;
    }

    public UserDTO getSender() {
        return sender;
    }

    public void setSender(UserDTO sender) {
        this.sender = sender;
    }

    public List<UserDTO> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<UserDTO> recipients) {
        this.recipients = recipients;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void addRecipient(UserDTO user) {
        if (recipients == null) recipients = new LinkedList<>();
        recipients.add(user);
    }

    public int getNumberOfRecipients() {
        if (recipients == null) return 0;
        return recipients.size();
    }
}
