package gate.rest;

import gate.annotation.Authorization;
import gate.annotation.Current;
import gate.annotation.Secure;
import gate.entity.User;
import gate.error.ForbiddenException;
import gate.error.UnauthorizedException;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

@Secure
@Dependent
@Interceptor
public class AuthorizationInterceptor
{
	@Inject
	@Current
	@RequestScoped
	User user;

	@AroundInvoke
	public Object secure(InvocationContext ctx) throws Exception
	{
		if (user == null || user.getId() == null)
			throw new UnauthorizedException();

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
