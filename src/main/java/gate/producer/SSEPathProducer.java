package gate.producer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

@ApplicationScoped
public class SSEPathProducer
{
	@Produces
	@Named("SSE")
	public String get(HttpServletRequest request)
	{
		return request.getContextPath() + "/SSE";
	}
}
