package gate.producer;

import gate.annotation.Current;
import gate.entity.User;
import gate.error.InvalidCredentialsException;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@ApplicationScoped
public class UserProducer
{

	@Current
	@Produces
	@RequestScoped
	@Named(value = "user")
	public User getUser(HttpServletRequest request) throws InvalidCredentialsException
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
