package gate;

import gate.annotation.Secure;
import gate.entity.User;
import gate.http.ScreenServletRequest;
import gate.io.Credentials;
import gate.util.SystemProperty;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.servlet.http.HttpServletRequest;

@Secure
@Dependent
@Interceptor
@Priority(Interceptor.Priority.APPLICATION + 10)
public class AuthenticationInterceptor
{

	private final String developer
			= SystemProperty.get("gate.developer").orElse(null);

	@Inject
	GateControl control;

	@Inject
	HttpServletRequest httpServletRequest;

	@AroundInvoke
	public Object secure(InvocationContext ctx) throws Exception
	{
		Request.set(httpServletRequest);
		ScreenServletRequest request = new ScreenServletRequest(httpServletRequest);

		var token = request.getBearerAuthorization()
				.map(e -> e.token()).orElse(null);
		if (token != null)
		{
			request.setAttribute(User.class.getName(),
					Credentials.of(token));
			return ctx.proceed();
		}

		var session = request.getSession(false);
		if (session != null)
		{
			request.setAttribute(User.class.getName(),
					session.getAttribute(User.class.getName()));
			return ctx.proceed();
		}

		if (developer != null)
			request.setAttribute(User.class.getName(),
					control.select(developer));
		return ctx.proceed();
	}

}
