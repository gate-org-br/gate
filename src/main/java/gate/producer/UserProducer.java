package gate.producer;

import gate.GateControl;
import gate.annotation.Current;
import gate.entity.User;
import gate.error.AuthenticationException;
import gate.error.HierarchyException;
import gate.error.UnauthorizedException;
import gate.http.BearerAuthorization;
import gate.http.CookieAuthorization;
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
	public User getUser(GateControl control,
		Credentials credentials,
		HttpServletRequest httpServletRequest) throws HierarchyException
	{
		if (httpServletRequest == null)
			return new User();

		if (httpServletRequest.getAttribute(User.class.getName()) instanceof User user)
			return user;

		try
		{
			ScreenServletRequest request
				= new ScreenServletRequest(httpServletRequest);

			var auth = request.getAuthorization();
			if (auth instanceof BearerAuthorization bearer)
			{
				User user = control.select(credentials.toSubject(bearer.token()));
				request.setAttribute(User.class.getName(), user);
				return user;
			} else if (auth instanceof CookieAuthorization cookie)
			{
				User user = control.select(credentials.toSubject(cookie.token()));
				request.setAttribute(User.class.getName(), user);
				return user;
			} else
				return new User();
		} catch (AuthenticationException
			| UnauthorizedException ex)
		{
			return new User();
		}
	}

}
