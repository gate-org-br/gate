package gate;

import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*")
public class ResponseCaptureFilter implements Filter
{

	private static final ThreadLocal<HttpServletResponse> RESPONSE_HOLDER = new ThreadLocal<>();

	public static HttpServletResponse getResponse()
	{
		return RESPONSE_HOLDER.get();
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
		throws IOException, ServletException
	{
		try
		{
			RESPONSE_HOLDER.set((HttpServletResponse) response);
			chain.doFilter(request, response);
		} finally
		{
			RESPONSE_HOLDER.remove();
		}
	}

	@RequestScoped
	public static class HttpServletResponseProducer
	{

		@Produces
		@RequestScoped
		public HttpServletResponse produceResponse()
		{
			return ResponseCaptureFilter.getResponse();
		}
	}
}
