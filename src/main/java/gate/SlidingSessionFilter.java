package gate;

import gate.annotation.Current;
import gate.entity.User;
import gate.error.UnauthorizedException;
import gate.http.BearerAuthorization;
import gate.http.CookieAuthorization;
import gate.http.ScreenServletRequest;
import gate.http.ScreenServletResponse;
import gate.security.Credentials;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter(value = "/*", asyncSupported = true)
public class SlidingSessionFilter implements Filter
{
	@Inject
	Credentials credentials;

	@Inject
	@Current
	Instance<User> userInstance;

	@Override
	public void doFilter(ServletRequest servletRequest,
	                     ServletResponse servletResponse,
	                     FilterChain chain)
			throws IOException, ServletException
	{
		servletRequest.setCharacterEncoding("UTF-8");
		servletResponse.setCharacterEncoding("UTF-8");
		servletResponse.setLocale(servletRequest.getLocale());

		if (!(servletRequest instanceof HttpServletRequest httpServletRequest)
		    || !(servletResponse instanceof HttpServletResponse httpServletResponse))
		{
			chain.doFilter(servletRequest, servletResponse);
			return;
		}

		var request = new ScreenServletRequest(httpServletRequest);
		if (request.isStaticRequest())
		{
			chain.doFilter(servletRequest, servletResponse);
			return;
		}

		var user = userInstance.get();
		chain.doFilter(servletRequest, servletResponse);
		if (httpServletResponse.isCommitted())
			return;

		var authorization = request.getAuthorization();
		if (authorization == null)
			return;

		var token = authorization.token();
		if (token == null)
			return;

		var response = new ScreenServletResponse(httpServletResponse);
		if (user == null || user.getId() == null)
		{
			if (authorization instanceof CookieAuthorization)
				response.revokeSessionCookie(request);
			return;
		}

		try
		{
			token = credentials.refresh(token);
			if (authorization instanceof CookieAuthorization)
				response.createSessionCookie(request, token);
			else if (authorization instanceof BearerAuthorization)
				httpServletResponse.addHeader("X-Access-Token", token);
		} catch (UnauthorizedException ex)
		{
			if (authorization instanceof CookieAuthorization)
				response.revokeSessionCookie(request);
		}
	}
}