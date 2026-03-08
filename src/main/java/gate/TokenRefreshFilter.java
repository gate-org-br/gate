package gate;

import gate.entity.User;
import gate.http.BearerAuthorization;
import gate.http.CookieAuthorization;
import gate.http.ScreenServletRequest;
import gate.security.Credentials;
import jakarta.inject.Inject;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Locale;
import java.util.Set;

@WebFilter(value = "/*", asyncSupported = true)
public class TokenRefreshFilter implements Filter
{

	@Inject
	Credentials credentials;

	private static final Set<String> STATIC_EXTENSIONS = Set.of(
			".css", ".js", ".png", ".jpg", ".jpeg", ".gif", ".ico",
			".svg", ".woff", ".woff2", ".ttf", ".eot"
	);

	@Override
	public void doFilter(ServletRequest servletRequest,
						 ServletResponse servletResponse,
						 FilterChain chain)
			throws IOException, ServletException
	{
		servletRequest.setCharacterEncoding("UTF-8");
		servletResponse.setCharacterEncoding("UTF-8");
		servletResponse.setLocale(Locale.getDefault());

		chain.doFilter(servletRequest, servletResponse);

		if (!(servletRequest instanceof HttpServletRequest request))
			return;

		if (!(servletResponse instanceof HttpServletResponse response))
			return;

		if (response.isCommitted())
			return;

		String uri = request.getRequestURI().toLowerCase();
		if (STATIC_EXTENSIONS.stream().anyMatch(uri::endsWith))
			return;

		Object attr = request.getAttribute(User.class.getName());
		if (attr instanceof User user && user.getId() != null)
		{
			try
			{
				var authorization = new ScreenServletRequest(request).getAuthorization();
				if (authorization instanceof CookieAuthorization cookie)
					response.addCookie(CookieFactory.create(
							Gate.SUBJECT_COOKIE, credentials.refresh(cookie.token())));
				else if (authorization instanceof BearerAuthorization bearer)
					response.addHeader("X-Access-Token", credentials.refresh(bearer.token()));
			} catch (RuntimeException ex)
			{
				response.addCookie(CookieFactory.delete(Gate.SUBJECT_COOKIE));
			}
		}
	}
}