package gate;

import gate.annotation.Authorization;
import gate.annotation.Current;
import gate.annotation.Secure;
import gate.entity.User;
import gate.error.ForbiddenException;
import gate.error.UnauthorizedException;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

@Secure
@Dependent
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class AuthorizationInterceptor
{
	@Inject
	@Current
	Instance<User> userInstance;

	@AroundInvoke
	public Object secure(InvocationContext ctx) throws Exception
	{
		var user = userInstance.get();

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

		GateContext.init(new GateContext(user));
		try
		{
			return ctx.proceed();
		} finally {GateContext.close();}
	}
}