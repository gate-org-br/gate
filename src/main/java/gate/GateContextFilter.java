package gate;

import gate.annotation.Current;
import gate.entity.User;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.Locale;

@WebFilter(value = "/*", asyncSupported = true)
public class GateContextFilter implements Filter
{
	@Inject
	GateContext gateContext;

	@Inject
	@Current
	Instance<User> userInstance;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException
	{
		if (request instanceof HttpServletRequest httpServletRequest)
			try
			{
				gateContext.set(User.class, userInstance.get());
				gateContext.set(Locale.class, httpServletRequest.getLocale());
				chain.doFilter(request, response);
			} finally
			{
				gateContext.clear();
			}
	}
}