package gate.producer;

import gate.CookieFactory;
import gate.Gate;
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
import jakarta.servlet.http.HttpServletResponse;

public class UserProducer
{
	@Current
	@Produces
	@RequestScoped
	@Named(value = "user")
	public User getUser(Credentials credentials,
						HttpServletRequest httpServletRequest,
						HttpServletResponse response) throws HierarchyException
	{
		if (httpServletRequest == null || response == null)
			return new User();

		if (httpServletRequest.getAttribute(User.class.getName())
				instanceof User user)
			return user;

		try
		{
			ScreenServletRequest request =
					new ScreenServletRequest(httpServletRequest);

			var auth = request.getAuthorization();
			if (auth instanceof BearerAuthorization bearer)
			{
				User user = credentials.subject(bearer.token());
				request.setAttribute(User.class.getName(), user);
				return user;
			} else if (auth instanceof CookieAuthorization cookie)
			{
				User user = credentials.subject(cookie.token());
				request.setAttribute(User.class.getName(), user);
				response.addCookie(CookieFactory.create(Gate.SUBJECT_COOKIE,
						credentials.subject(user)));
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
