package gate.messaging;

import gate.type.mime.MimeMail;
import java.time.LocalDateTime;

public class Message
{

    private final LocalDateTime date;
    private final String sender;
    private final String receiver;
    private final MimeMail data;

    public Message(LocalDateTime date, String sender, String receiver, MimeMail data)
    {
	this.date = date;
	this.sender = sender;
	this.receiver = receiver;
	this.data = data;
    }

    public LocalDateTime getDate()
    {
	return date;
    }

    public String getReceiver()
    {
	return receiver;
    }

    public String getSender()
    {
	return sender;
    }

    public MimeMail getData()
    {
	return data;
    }
}
