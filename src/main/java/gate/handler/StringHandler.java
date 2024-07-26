package gate.handler;

import gate.Progress;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ApplicationScoped
public class StringHandler implements Handler
{

	@Inject
	private JSPCommandHandler jspHandler;

	@Inject
	private HTMLCommandHandler htmlHandler;

	@Inject
	private TextHandler textHandler;

	@Inject
	private JSHandler jsHandler;

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, Object value)
	{
		String string = value.toString();
		if (string.endsWith(".jsp"))
			jspHandler.handle(request, response, string);
		else if (string.endsWith(".html"))
			htmlHandler.handle(request, response, string);
		else if (string.endsWith(".js"))
			jsHandler.handle(request, response, string);
		else
			textHandler.handle(request, response, string);

	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
		Progress progress, Object value)
	{
		String string = value.toString();
		if (string.endsWith(".jsp"))
			jspHandler.handle(request, response, progress, string);
		else if (string.endsWith(".html"))
			htmlHandler.handle(request, response, progress, string);
		else
			textHandler.handle(request, response, progress, string);
	}
}
