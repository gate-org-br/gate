package gate.producer;

import gate.Session;
import gate.annotation.Current;
import gate.entity.User;
import gate.error.InvalidCredentialsException;
import gate.io.Credentials;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

@RequestScoped
public class UserProducer
{

	@Inject
	private Session session;

	@Inject
	private HttpServletRequest request;

	@Current
	@Produces
	@Named(value = "user")
	public User getUser()
	{
		try
		{
			return Credentials.of(request)
				.orElseGet(() -> session.getUser());
		} catch (InvalidCredentialsException ex)
		{
			return null;
		}
	}
}
