package gate.handler;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ApplicationScoped
public class StringHandler implements Handler
{

	@Inject
	private JSPCommandHandler jspHandler;

	@Inject
	private HTMLCommandHandler htmlHandler;

	@Inject
	private TextHandler textHandler;

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, Object value)
	{
		String string = value.toString();
		if (string.endsWith(".jsp"))
			jspHandler.handle(request, response, string);
		else if (string.endsWith(".html"))
			htmlHandler.handle(request, response, value);
		else
			textHandler.handle(request, response, value);
	}
}
