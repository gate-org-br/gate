package gate.rest;

import gate.GateControl;
import gate.annotation.Authorization;
import gate.annotation.Secure;
import gate.entity.User;
import gate.io.Credentials;
import gate.util.SystemProperty;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.ForbiddenException;

@Secure
@Dependent
@Interceptor
public class AuthorizationInterceptor
{

	@Inject
	private HttpServletRequest request;
	private final String developer
		= SystemProperty.get("gate.developer").orElse(null);

	@Inject
	GateControl control;

	@AroundInvoke
	public Object secure(InvocationContext ctx) throws Exception
	{

		User user = null;
		if (Credentials.isPresent(request))
		{
			user = Credentials.of(request).orElseThrow();
			request.setAttribute(User.class.getName(), user);
		} else if (request.getSession(false) != null)
			user = (User) request.getSession()
				.getAttribute(User.class.getName());
		else if (developer != null)
			request.getSession()
				.setAttribute(User.class.getName(),
					user = control.select(developer));

		if (user == null)
			throw new ForbiddenException();

		Authorization.Value authorization = Authorization.Extractor.extract(ctx.getMethod(),
			ctx.getMethod().getDeclaringClass().getName(),
			ctx.getMethod().getDeclaringClass().getSimpleName(),
			ctx.getMethod().getName());
		if (!user.checkAccess(authorization.module(),
			authorization.screen(),
			authorization.action()))
			throw new ForbiddenException();

		return ctx.proceed();
	}
}
