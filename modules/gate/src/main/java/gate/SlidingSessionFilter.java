package gate;

import gate.catalog.SessionCatalog;
import gate.error.UnauthorizedException;
import gate.http.BearerAuthentication;
import gate.http.CookieAuthentication;
import gate.http.ScreenServletRequest;
import gate.http.ScreenServletResponse;
import gate.security.Credentials;
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
	SessionCatalog sessionCatalog;

	@Override
	public void doFilter(ServletRequest servletRequest,
	                     ServletResponse servletResponse,
	                     FilterChain chain)
			throws IOException, ServletException
	{
		servletRequest.setCharacterEncoding("UTF-8");
		servletResponse.setCharacterEncoding("UTF-8");
		servletResponse.setLocale(servletRequest.getLocale());

		if (servletRequest instanceof HttpServletRequest httpServletRequest
		    && servletResponse instanceof HttpServletResponse httpServletResponse)
		{
			var request = new ScreenServletRequest(httpServletRequest);
			if (!request.isStaticRequest())
			{
				var authorization = request.getAuthentication();
				if (authorization != null)
				{
					var token = authorization.token();
					if (token != null)
					{
						var response = new ScreenServletResponse(httpServletResponse);

						try
						{
							var credentials = Credentials.parse(token);
							if (credentials.sid() != null && !sessionCatalog.exists(credentials))
								throw new UnauthorizedException("Attempt to authenticate with invalid credentials");
							request.setAttribute(Credentials.class.getName(), credentials);

							token = credentials.refresh().toString();
							if (authorization instanceof CookieAuthentication)
								response.createSessionCookie(request, token);
							else if (authorization instanceof BearerAuthentication)
								httpServletResponse.addHeader("X-Access-Token", token);
						} catch (UnauthorizedException ex)
						{
							if (authorization instanceof CookieAuthentication)
								response.revokeSessionCookie(request);
							httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
							return;
						}
					}
				}
			}
		}

		chain.doFilter(servletRequest, servletResponse);
	}
}