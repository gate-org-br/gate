package gate.event;

import gate.converter.Converter;
import gate.entity.Chat;
import gate.entity.User;
import gate.lang.json.JsonObject;

public class ChatEvent extends AppEvent
{

	private final Chat msg;

	@Override
	public boolean checkAccess(User user)
	{
		return user == null
			|| user.equals(msg.getSender())
			|| user.equals(msg.getReceiver());
	}

	public ChatEvent(Chat msg)
	{
		super(new JsonObject()
			.set("id", null)
			.set("sender", new JsonObject()
				.setString("id", msg.getSender().getId().toString())
				.setString("name", msg.getSender().getName()))
			.set("receiver", new JsonObject()
				.setString("id", msg.getReceiver().getId().toString())
				.setString("name", msg.getReceiver().getName()))
			.setString("date", Converter.toText(msg.getDate()))
			.setString("text", msg.getText())
			.setString("status", msg.getStatus().name()));
		this.msg = msg;
	}

}
