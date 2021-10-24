package gate.event;

import gate.entity.User;

public class LoginEvent
{

	private final User user;

	public LoginEvent(User user)
	{
		this.user = user;
	}

	public User getUser()
	{
		return user;
	}
}
