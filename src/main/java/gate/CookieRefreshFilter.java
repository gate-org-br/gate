package gate;

import gate.http.CookieAuthorization;
import gate.http.ScreenServletRequest;
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

@WebFilter(value = "/*", asyncSupported = true)
public class CookieRefreshFilter implements Filter
{

	@Inject
	Credentials credentials;

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
			try
			{
				ScreenServletRequest screenServletRequest
					= new ScreenServletRequest(httpServletRequest);

				if (screenServletRequest.getAuthorization() instanceof CookieAuthorization cookie)
					httpServletResponse.addHeader("Set-Cookie",
						CookieFactory.create(credentials.refresh(cookie.token())));

			} catch (RuntimeException ex)
			{
				httpServletResponse.addHeader("Set-Cookie",
					CookieFactory.create(CookieFactory.delete()));
			}
		}

		chain.doFilter(servletRequest, servletResponse);
	}
}
