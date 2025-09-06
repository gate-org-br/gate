package gate.producer;

import gate.GateControl;
import gate.annotation.Current;
import gate.entity.User;
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
				var subject = credentials.toSubject(bearer.token());
				User user = control.select(subject.id());
				if (user.getActivity() == null || user.getActivity().isAfter(subject.iat()))
					throw new UnauthorizedException("Attempt to authenticate with invalid token");
				request.setAttribute(User.class.getName(), user);
				return user;
			} else if (auth instanceof CookieAuthorization cookie)
			{
				var subject = credentials.toSubject(cookie.token());
				User user = control.select(subject.id());
				request.setAttribute(User.class.getName(), user);
				if (user.getActivity() == null || user.getActivity().isAfter(subject.iat()))
					throw new UnauthorizedException("Attempt to authenticate with invalid token");
				return user;
			} else
				return new User();
		} catch (RuntimeException ex)
		{
			return new User();
		}
	}

}
