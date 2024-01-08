package gate.producer;

import gate.Request;
import gate.annotation.Current;
import gate.entity.User;
import gate.error.InvalidCredentialsException;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

public class UserProducer
{

	@Current
	@Produces
	@Named(value = "user")
	public User getUser() throws InvalidCredentialsException
	{
		User user = (User) Request.get().getAttribute(User.class.getName());
		if (user != null)
			return user;

		HttpSession session = Request.get().getSession(false);
		if (session != null)
			return (User) session.getAttribute(User.class.getName());

		return null;
	}
}
