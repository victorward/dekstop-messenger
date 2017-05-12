package zaawjava.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "private_messages")
public class ChatMessage implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn (name="conversation_id",referencedColumnName="conversation_id",nullable=false,unique=false)
	private Conversation conversation;

	@Column(name = "content")
	private String content;

	@Column(name="send_date")
	private Date date;

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name="sender", nullable=false)
    private User sender;

	public ChatMessage()
	{
		super();
	}



	public ChatMessage(Conversation conversation, String content, User sender)
	{
		super();
		this.conversation = conversation;
		this.content = content;
		this.date =  new Date();
		this.sender = sender;
	}



	public User getSender()
	{
		return sender;
	}

	public void setSender(User sender)
	{
		this.sender = sender;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public Conversation getConversation()
	{
		return conversation;
	}

	public void setConversation(Conversation conversation)
	{
		this.conversation = conversation;
	}

	public String getContent()
	{
		return content;
	}

	public void setContent(String content)
	{
		this.content = content;
	}

	public Date getDate()
	{
		return date;
	}

	public void setDate(Date date)
	{
		this.date = date;
	}

	@Override
	public String toString()
	{
		return "PrivateMessage [id=" + id + ", conversation=" + conversation.getId() + ", content=" + content + ", date=" + date
				+ ", sender=" + sender.getId() + "]";
	}



}
