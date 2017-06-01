package zaawjava.server.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "conversations")
public class Conversation implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "conversation_id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "user1")
    private User user1;

    @ManyToOne
    @JoinColumn(name = "user2")
    private User user2;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "conversation")
    @OrderBy("date ASC")
    private List<ChatMessage> privateMessages = new ArrayList<ChatMessage>(0);


    public List<ChatMessage> getPrivateMessages() {
        return privateMessages;
    }

    public void setPrivateMessages(List<ChatMessage> privateMessages) {
        this.privateMessages = privateMessages;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser1() {
        return user1;
    }

    public void setUser1(User user1) {
        this.user1 = user1;
    }

    public User getUser2() {
        return user2;
    }

    public void setUser2(User user2) {
        this.user2 = user2;
    }

    public Conversation(User user1, User user2) {
        super();
        this.user1 = user1;
        this.user2 = user2;
    }

    public Conversation() {
        super();
    }

    @Override
    public String toString() {
        return "Conversation [id=" + id + ", user1=" + user1.getId() + ", user2=" + user2.getId() + "]";
    }


}
