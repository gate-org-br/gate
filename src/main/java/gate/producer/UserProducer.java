package gate.producer;

import gate.annotation.Current;
import gate.entity.User;
import gate.error.InvalidCredentialsException;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class UserProducer
{

	@Inject
	private HttpServletRequest request;

	@Current
	@Produces
	@Named(value = "user")
	public User getUser() throws InvalidCredentialsException
	{
		User user = (User) request.getAttribute(User.class.getName());
		if (user != null)
			return user;

		HttpSession session = request.getSession(false);
		if (session != null)
			return (User) session.getAttribute(User.class.getName());

		return null;
	}
}
