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
				.setInt("id", msg.getSender().getId().getValue())
				.setString("name", msg.getSender().getName()))
			.set("receiver", new JsonObject()
				.setInt("id", msg.getReceiver().getId().getValue())
				.setString("name", msg.getReceiver().getName()))
			.setString("date", Converter.toText(msg.getDate()))
			.setString("text", msg.getText()));
		this.msg = msg;
	}

}
