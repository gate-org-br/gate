package gate.producer;

import gate.Session;
import gate.annotation.Current;
import gate.entity.User;
import gate.security.Credentials;
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
		return Credentials.of(request)
			.orElseGet(() -> session.getUser());
	}
}
