package gate.producer;

import gate.GateContext;
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
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

@ApplicationScoped
public class UserProducer
{
	@Current
	@Produces
	@Dependent
	@Named(value = "user")
	public User getUser(GateControl control, Credentials credentials, GateContext gateContext, Instance<HttpServletRequest> requestInstance)
			throws HierarchyException
	{
		return gateContext.get(User.class)
				.orElseGet(() -> getUserFromRequest(control, credentials, gateContext, requestInstance));
	}

	private User getUserFromRequest(GateControl control, Credentials credentials, GateContext gateContext, Instance<HttpServletRequest> requestInstance)
	{
		if (requestInstance.isUnsatisfied())
			return new User();

		var httpServletRequest = requestInstance.get();
		if (httpServletRequest.getAttribute(User.class.getName()) instanceof User user)
			return user;

		try
		{
			ScreenServletRequest request = new ScreenServletRequest(httpServletRequest);

			var auth = request.getAuthorization();
			if (auth instanceof BearerAuthorization bearer)
				return getUser(control, credentials, gateContext, httpServletRequest, bearer.token());

			if (auth instanceof CookieAuthorization cookie)
				return getUser(control, credentials, gateContext, httpServletRequest, cookie.token());

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
	                     GateContext gateContext, HttpServletRequest httpServletRequest, String token)
	{
		var subject = credentials.toToken(token);
		User user = control.select(subject.id());
		if (user.getActivity() != null && subject.iat().isBefore(user.getActivity()))
			throw new UnauthorizedException("Attempt to authenticate with invalid token");
		httpServletRequest.setAttribute(User.class.getName(), user);
		gateContext.set(User.class, user);
		return user;
	}
}
