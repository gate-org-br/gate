package gate.rest;

import gate.annotation.Authorization;
import gate.annotation.Secure;
import gate.entity.User;
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

	@AroundInvoke
	public Object secure(InvocationContext ctx) throws Exception
	{

		User user = request.getSession(false) != null
			? (User) request.getSession().getAttribute(User.class.getName())
			: (User) request.getAttribute(User.class.getName());

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
