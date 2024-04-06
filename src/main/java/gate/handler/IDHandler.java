package gate.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ApplicationScoped
public class IDHandler implements Handler
{

	@Inject
	private TextHandler textHandler;

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, Object value)
	{
		textHandler.handle(request, response, value.toString());
	}
}
