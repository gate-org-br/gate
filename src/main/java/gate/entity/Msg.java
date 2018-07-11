package gate.entity;

import gate.annotation.Entity;
import gate.annotation.Icon;
import gate.type.DateTime;
import gate.type.ID;
import gate.type.mime.MimeMail;

import java.io.Serializable;

@Entity
@Icon("2034")
public class Msg implements Serializable
{

	private static final long serialVersionUID = 1L;
	private ID id;
	private String sender;
	private String receiver;
	private DateTime dispatch;
	private MimeMail mimeMail;
	private String exception;
	private Integer failures;

	public Msg()
	{

	}

	public Msg(MimeMail mimeMail, String sender, String receiver)
	{
		this.mimeMail = mimeMail;
		this.receiver = receiver;
		this.sender = sender;
		this.dispatch = new DateTime();
		this.failures = 0;
	}

	public Msg(MimeMail mimeMail, String receiver)
	{
		this(mimeMail, receiver, null);
	}

	public ID getId()
	{
		return id;
	}

	public void setId(ID id)
	{
		this.id = id;
	}

	public String getReceiver()
	{
		return receiver;
	}

	public void setReceiver(String receiver)
	{
		this.receiver = receiver;
	}

	public String getSender()
	{
		return sender;
	}

	public void setSender(String sender)
	{
		this.sender = sender;
	}

	public String getException()
	{
		return exception;
	}

	public void setException(String exception)
	{
		this.exception = exception;
	}

	public Integer getFailures()
	{
		return failures;
	}

	public void setFailures(Integer failures)
	{
		this.failures = failures;
	}

	public DateTime getDispatch()
	{
		return dispatch;
	}

	public void setDispatch(DateTime dispatch)
	{
		this.dispatch = dispatch;
	}

	public MimeMail getMimeMail()
	{
		return mimeMail;
	}

	public void setMimeMail(MimeMail mimeMail)
	{
		this.mimeMail = mimeMail;
	}
}
