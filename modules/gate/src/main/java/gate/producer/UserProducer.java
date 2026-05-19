package gate.producer;

import gate.annotation.Current;
import gate.catalog.UserCatalog;
import gate.entity.User;
import gate.security.Credentials;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

@ApplicationScoped
public class UserProducer
{
	@Inject
	UserCatalog userCatalog;

	@Current
	@Produces
	@RequestScoped
	@Named(value = "user")
	public User getUser(HttpServletRequest request)
	{
		if (request.getAttribute(User.class.getName()) instanceof User user)
			return user;

		if (!(request.getAttribute(Credentials.class.getName()) instanceof Credentials credentials))
			return new User();

		User user = credentials.stateless()
				? credentials.usr()
				: userCatalog.select(credentials.sub());

		request.setAttribute(User.class.getName(), user);
		return user;
	}
}