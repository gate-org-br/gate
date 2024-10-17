package gate.rest;

import gate.GateControl;
import gate.annotation.Authorization;
import gate.annotation.Public;
import gate.annotation.Secure;
import gate.entity.User;
import gate.http.ScreenServletRequest;
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
	GateControl control;

	@Inject
	HttpServletRequest httpServletRequest;

	@AroundInvoke
	public Object secure(InvocationContext ctx) throws Exception
	{

		ScreenServletRequest request
				= new ScreenServletRequest(httpServletRequest);
		control.authenticate(request);
		User user = request.getUser();

		if (user == null
				&& !ctx.getMethod().isAnnotationPresent(Public.class))
			throw new ForbiddenException();

		Authorization.Value authorization = Authorization.Extractor.extract(ctx.getMethod(),
				ctx.getMethod().getDeclaringClass().getName(),
				ctx.getMethod().getDeclaringClass().getSimpleName(),
				ctx.getMethod().getName());

		if (user != null)
			if (!user.checkAccess(authorization.module(),
					authorization.screen(),
					authorization.action()))
				throw new ForbiddenException();

		return ctx.proceed();
	}

}
