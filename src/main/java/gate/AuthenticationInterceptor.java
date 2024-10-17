package gate;

import gate.annotation.Secure;
import gate.entity.User;
import gate.http.BearerAuthorization;
import gate.http.CookieAuthorization;
import gate.http.ScreenServletRequest;
import gate.io.Credentials;
import gate.io.Developer;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Secure
@Dependent
@Interceptor
@Priority(100)
public class AuthenticationInterceptor
{

	@Inject
	Developer developer;

	@Inject
	Credentials credentials;

	@Inject
	HttpServletResponse response;

	@Inject
	HttpServletRequest httpServletRequest;

	@AroundInvoke
	public Object secure(InvocationContext ctx) throws Exception
	{
		ScreenServletRequest request = new ScreenServletRequest(httpServletRequest);
		Request.set(request);

		var auth = request.getAuthorization();
		if (auth instanceof BearerAuthorization bearer)
		{
			User user = credentials.subject(bearer.token());
			request.setAttribute(User.class.getName(), user);
			return ctx.proceed();
		}

		if (auth instanceof CookieAuthorization cookie)
		{
			User user = credentials.subject(cookie.token());
			request.setAttribute(User.class.getName(), user);
			response.addCookie(CookieFactory.create(credentials.subject(user)));
			return ctx.proceed();
		}

		User user = developer.get().orElse(null);
		request.setAttribute(User.class.getName(), user);
		return ctx.proceed();
	}

}
