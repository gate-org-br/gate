package gate.rest;

import gate.annotation.Secure;
import gate.entity.User;
import gate.io.Credentials;
import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.servlet.http.HttpServletRequest;

@Secure
@Dependent
@Interceptor
@Priority(Interceptor.Priority.APPLICATION + 10)
public class AuthenticationInterceptor
{

	@Inject
	HttpServletRequest request;

	@AroundInvoke
	public Object secure(InvocationContext ctx) throws Exception
	{
		if (request.getSession(false) == null
			&& Credentials.isPresent(request))
		{
			User user = Credentials.of(request).orElseThrow();
			request.setAttribute(User.class.getName(), user);
		}

		return ctx.proceed();
	}
}
