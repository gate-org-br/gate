package gate.producer;

import gate.annotation.Current;
import gate.catalog.SessionCatalog;
import gate.catalog.UserCatalog;
import gate.entity.User;
import gate.error.HierarchyException;
import gate.error.UnauthorizedException;
import gate.http.ScreenServletRequest;
import gate.security.Credentials;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

@RequestScoped
public class UserProducer
{
	@Current
	@Produces
	@RequestScoped
	@Named(value = "user")
	public User getUser(HttpServletRequest httpServletRequest) throws HierarchyException
	{
		if (httpServletRequest == null)
			return new User();

		if (httpServletRequest.getAttribute(User.class.getName()) instanceof User user)
			return user;

		ScreenServletRequest request = new ScreenServletRequest(httpServletRequest);

		var auth = request.getAuthorization();
		if (auth == null)
			return new User();

		var token = auth.token();
		if (token == null)
			return new User();

		var credentials = Credentials.parse(token);
		if (credentials.revocable() && !SessionCatalog.exists(credentials))
			throw new UnauthorizedException("Attempt to authenticate with invalid credentials");

		User user = credentials.stateless() ? credentials.usr() : UserCatalog.select(credentials.sub());

		httpServletRequest.setAttribute(User.class.getName(), user);
		return user;
	}
}