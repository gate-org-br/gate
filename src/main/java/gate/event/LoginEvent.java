package gate.event;

import gate.entity.User;
import gate.lang.json.JsonObject;

public class LoginEvent extends AppEvent
{

	private final User user;

	public LoginEvent(User user)
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
