package gate.entity;

import gate.type.ID;
import java.time.LocalDateTime;

public class Chat
{

	private ID id;
	private LocalDateTime date;
	private User sender;
	private User receiver;
	private String text;

	public ID getId()
	{
		return id;
	}

	public Chat setId(ID id)
	{
		this.id = id;
		return this;
	}

	public LocalDateTime getDate()
	{
		return date;
	}

	public Chat setDate(LocalDateTime date)
	{
		this.date = date;
		return this;
	}

	public User getSender()
	{
		return sender;
	}

	public Chat setSender(User sender)
	{
		this.sender = sender;
		return this;
	}

	public User getReceiver()
	{
		return receiver;
	}

	public Chat setReceiver(User receiver)
	{
		this.receiver = receiver;
		return this;
	}

	public String getText()
	{
		return text;
	}

	public Chat setText(String text)
	{
		this.text = text;
		return this;
	}

}
