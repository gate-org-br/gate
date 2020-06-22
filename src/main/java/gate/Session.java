package gate;

import gate.entity.User;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;

@SessionScoped
public class Session implements Serializable
{

	private User user;

	public User getUser()
	{
		return user;
	}

	void setUser(User user)
	{
		this.user = user;
	}

}
