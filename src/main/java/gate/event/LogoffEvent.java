package gate.event;

import gate.entity.User;
import gate.lang.json.JsonObject;

public class LogoffEvent extends AppEvent
{

	private final User user;

	public LogoffEvent(User user)
	{
		super(new JsonObject()
			.setString("id", user.getId().toString()));
		this.user = user;
	}

	public User getUser()
	{
		return user;
	}

}
