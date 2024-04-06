package gate;

import gate.annotation.Secure;
import gate.entity.User;
import gate.io.Credentials;
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

	@Inject
	HttpServletRequest request;

	@AroundInvoke
	public Object secure(InvocationContext ctx) throws Exception
	{
		Request.set(request);

		if (request.getSession(false) == null
			&& Credentials.isPresent(request))
		{
			User user = Credentials.of(request).orElseThrow();
			request.setAttribute(User.class.getName(), user);
		}

		return ctx.proceed();
	}
}
