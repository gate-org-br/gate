package gate.producer;

import gate.GateControl;
import gate.annotation.Current;
import gate.cache.Cache;
import gate.converter.Converter;
import gate.entity.User;
import gate.error.AuthenticationException;
import gate.error.HierarchyException;
import gate.error.UnauthorizedException;
import gate.http.BearerAuthorization;
import gate.http.CookieAuthorization;
import gate.http.ScreenServletRequest;
import gate.security.Credentials;
import gate.util.SystemProperty;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;

@RequestScoped
public class UserProducer
{

	@Current
	@Produces
	@RequestScoped
	@Named(value = "user")
	public User getUser(GateControl control, Credentials credentials, HttpServletRequest httpServletRequest)
			throws HierarchyException
	{
		if (httpServletRequest == null)
			return new User();

		if (httpServletRequest.getAttribute(User.class.getName()) instanceof User user)
			return user;

		try
		{
			ScreenServletRequest request = new ScreenServletRequest(httpServletRequest);

			var auth = request.getAuthorization();
			if (auth instanceof BearerAuthorization bearer)
				return getUser(control, credentials, httpServletRequest, bearer.token());

			if (auth instanceof CookieAuthorization cookie)
				return getUser(control, credentials, httpServletRequest, cookie.token());

			return new User();
		} catch (AuthenticationException | UnauthorizedException ex)
		{
			throw ex;
		} catch (RuntimeException ex)
		{
			return new User();
		}
	}

	private User getUser(GateControl control, Credentials credentials,
			HttpServletRequest httpServletRequest, String token)
	{
		var subject = credentials.toToken(token);
		User user = control.select(subject.id());
		if (user.getActivity() != null && subject.iat().isBefore(user.getActivity()))
			throw new UnauthorizedException("Attempt to authenticate with invalid token");
		httpServletRequest.setAttribute(User.class.getName(), user);
		return user;
	}
}
