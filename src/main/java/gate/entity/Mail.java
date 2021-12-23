package gate.entity;

import gate.type.ID;
import gate.type.mime.MimeMail;
import java.time.LocalDateTime;

public class Mail
{

	private ID id;
	private String app;
	private int attempts;
	private String sender;
	private String receiver;
	private LocalDateTime date;
	private MimeMail<?> message;
	private LocalDateTime expiration;

	public String getApp()
	{
		return app;
	}

	public void setApp(String app)
	{
		this.app = app;
	}

	public ID getId()
	{
		return id;
	}

	public void setId(ID id)
	{
		this.id = id;
	}

	public String getSender()
	{
		return sender;
	}

	public void setSender(String sender)
	{
		this.sender = sender;
	}

	public LocalDateTime getExpiration()
	{
		return expiration;
	}

	public void setExpiration(LocalDateTime expiration)
	{
		this.expiration = expiration;
	}

	public String getReceiver()
	{
		return receiver;
	}

	public void setReceiver(String receiver)
	{
		this.receiver = receiver;
	}

	public MimeMail<?> getMessage()
	{
		return message;
	}

	public void setMessage(MimeMail<?> message)
	{
		this.message = message;
	}

	public LocalDateTime getDate()
	{
		return date;
	}

	public void setDate(LocalDateTime date)
	{
		this.date = date;
	}

	public int getAttempts()
	{
		return attempts;
	}

	public void setAttempts(int attempts)
	{
		this.attempts = attempts;
	}

}
