package gate.event;

import gate.entity.User;

public class LogoffEvent extends AppEvent
{

	private final User user;

	public LogoffEvent(User user)
	{
		this.user = user;
	}

	public User getUser()
	{
		return user;
	}

}
