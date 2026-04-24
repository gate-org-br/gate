package gate.i18n;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

@WebFilter(value = "/*", asyncSupported = true)
public class LocaleFilter implements Filter
{
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException
	{
		if (request instanceof HttpServletRequest httpServletRequest)
			try
			{
				CurrentLocale.set(httpServletRequest.getLocale());
				chain.doFilter(request, response);
			} finally
			{
				CurrentLocale.clear();
			}

	}
}