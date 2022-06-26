package gate.event;

import gate.entity.User;
import gate.lang.json.JsonObject;
import gate.type.ID;

public class ChatReceivedEvent extends AppEvent
{

	private final ID sender;
	private final ID receiver;

	@Override
	public boolean checkAccess(User user)
	{
		return user == null
			|| sender.equals(user.getId())
			|| receiver.equals(user.getId());
	}

	public ChatReceivedEvent(ID sender, ID receiver)
	{
		super(new JsonObject()
			.setString("sender", sender.toString())
			.setString("receiver", receiver.toString()));
		this.sender = sender;
		this.receiver = receiver;
	}

}
