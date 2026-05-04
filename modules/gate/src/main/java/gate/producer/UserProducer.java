package gate.producer;

import gate.annotation.Current;
import gate.catalog.UserCatalog;
import gate.entity.User;
import gate.security.Credentials;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

@RequestScoped
public class UserProducer
{
	@Inject
	UserCatalog userCatalog;

	@Current
	@Produces
	@RequestScoped
	@Named(value = "user")
	public User getUser(HttpServletRequest httpServletRequest)
	{
		if (httpServletRequest.getAttribute(User.class.getName()) instanceof User user)
			return user;

		var credentials = (Credentials) httpServletRequest.getAttribute(Credentials.class.getName());
		if (credentials == null)
			return new User();

		User user = credentials.stateless()
				? credentials.usr()
				: userCatalog.select(credentials.sub());

		httpServletRequest.setAttribute(User.class.getName(), user);
		return user;
	}
}