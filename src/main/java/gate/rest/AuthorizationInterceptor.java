package gate.rest;

import gate.annotation.Authorization;
import gate.annotation.Secure;
import gate.entity.User;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ForbiddenException;

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

		if (ctx.getMethod().isAnnotationPresent(Authorization.class))
		{
			Authorization.Value authorization = Authorization.Extractor.extract(ctx.getMethod());
			if (!user.checkAccess(authorization.module(),
				authorization.screen(),
				authorization.action()))
				throw new ForbiddenException();
		} else
		{
			String module = ctx.getMethod().getDeclaringClass().getName();
			String screen = ctx.getMethod().getDeclaringClass().getSimpleName();
			String action = ctx.getMethod().getName();
			if (!user.checkAccess(module, screen, action))
				throw new ForbiddenException();
		}

		return ctx.proceed();
	}
}