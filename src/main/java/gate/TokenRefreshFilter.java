package gate;

import gate.http.BearerAuthorization;
import gate.http.CookieAuthorization;
import gate.http.ScreenServletRequest;
import gate.http.ScreenServletResponse;
import gate.security.Credentials;
import jakarta.inject.Inject;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
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
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
		throws IOException, ServletException
	{
		servletRequest.setCharacterEncoding("UTF-8");
		servletResponse.setCharacterEncoding("UTF-8");
		servletResponse.setLocale(Locale.getDefault());

		if (servletRequest instanceof HttpServletRequest httpServletRequest
			&& servletResponse instanceof HttpServletResponse httpServletResponse)
		{
			String uri = httpServletRequest.getRequestURI().toLowerCase();

			ScreenServletRequest screenServletRequest
				= new ScreenServletRequest(httpServletRequest);
			ScreenServletResponse screenServletResponse
				= new ScreenServletResponse(httpServletResponse);

			if (!STATIC_EXTENSIONS.stream().anyMatch(ext -> uri.endsWith(ext)))
			{

				try
				{
					if (screenServletRequest.getAuthorization() instanceof CookieAuthorization cookie)
						screenServletResponse.createSubjectCookie(credentials.refresh(cookie.token()));
					else if (screenServletRequest.getAuthorization() instanceof BearerAuthorization bearer)
						httpServletResponse.addHeader("X-Access-Token", credentials.refresh(bearer.token()));

				} catch (RuntimeException ex)
				{
					screenServletResponse.deleteSubjectCookie();
				}

			}
		}

		chain.doFilter(servletRequest, servletResponse);
	}
}
