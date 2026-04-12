package gate.producer;

import gate.annotation.Current;
import gate.catalog.SessionCatalog;
import gate.catalog.UserCatalog;
import gate.entity.User;
import gate.error.AuthenticationException;
import gate.error.HierarchyException;
import gate.error.UnauthorizedException;
import gate.http.Authorization;
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

		try
		{
			ScreenServletRequest request = new ScreenServletRequest(httpServletRequest);

			var auth = request.getAuthorization();
			if (auth.type() != Authorization.Type.BEARER
			    && auth.type() != Authorization.Type.COOKIE)
				return new User();

			var credentials = Credentials.parse(auth.token());
			if (credentials.revocable() && !SessionCatalog.exists(credentials))
				throw new UnauthorizedException("Attempt to authenticate with invalid credentials");

			User user = credentials.stateless() ? credentials.usr() : UserCatalog.select(credentials.sub());

			httpServletRequest.setAttribute(User.class.getName(), user);
			return user;
		} catch (AuthenticationException | UnauthorizedException ex)
		{
			throw ex;
		} catch (RuntimeException ex)
		{
			return new User();
		}
	}
}