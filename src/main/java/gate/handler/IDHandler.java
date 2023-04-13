package gate.handler;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
