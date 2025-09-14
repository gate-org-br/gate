package gate.sse;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import gate.annotation.Current;
import gate.catcher.UnauthorizedExceptionCatcher;
import gate.entity.User;
import gate.error.UnauthorizedException;
import gate.http.ScreenServletRequest;
import jakarta.inject.Inject;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(value = "/SSE", asyncSupported = true)
public class SSEServlet extends HttpServlet
{

	@Inject
	@Current
	User user;

	@Inject
	UnauthorizedExceptionCatcher catcher;

	@Inject
	SSEClientManager sseClientManager;

	@Override
	protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse response)
			throws ServletException, IOException
	{

		ScreenServletRequest request = new ScreenServletRequest(httpServletRequest);

		try
		{
			if (user == null || user.getId() == null)
				throw new UnauthorizedException();

			response.setHeader("Connection", "keep-alive");
			response.setHeader("X-Accel-Buffering", "no");
			response.setContentType("text/event-stream; charset=UTF-8");
			response.setHeader("Cache-Control", "no-cache, no-transform");

			AsyncContext context = request.startAsync();
			context.setTimeout(TimeUnit.HOURS.toMillis(1));

			sseClientManager.addClient(user.unwrap(), context);
		} catch (UnauthorizedException ex)
		{
			catcher.catches(request, response, ex);
		}
	}
}
