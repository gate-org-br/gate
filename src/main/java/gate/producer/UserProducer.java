package gate.producer;

import gate.annotation.Current;
import gate.entity.User;
import gate.error.InvalidCredentialsException;
import gate.io.Credentials;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

public class UserProducer
{

	@Inject
	private HttpServletRequest request;

	@Current
	@Produces
	@Named(value = "user")
	public User getUser() throws InvalidCredentialsException
	{
		return Credentials.of(request)
			.orElseGet(() -> (User) request.getSession().getAttribute(User.class.getName()));
	}
}
