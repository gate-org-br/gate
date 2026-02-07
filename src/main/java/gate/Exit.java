package gate;

import gate.annotation.Current;
import gate.authenticator.Authenticator;
import gate.entity.User;
import gate.event.AppEvent;
import gate.event.LogoffEvent;
import gate.handler.HTMLCommandHandler;
import gate.handler.Handler;
import gate.http.ScreenServletRequest;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static gate.Gate.SUBJECT_COOKIE;

@WebServlet(value = "/Exit")
public class Exit extends HttpServlet
{

	@Inject
	Event<AppEvent> event;

	@Any
	@Inject
	Instance<Handler> handlers;

	@Inject
	@Current
	Instance<User> userInstance;

	@Inject
	@Current
	Authenticator authenticator;

	private static final String HTML = "/views/Exit.html";

	@Override
	public void service(HttpServletRequest httpServletRequest,
		HttpServletResponse response)
		throws ServletException, IOException
	{
		response.addHeader("Vary", "X-G-Fragment");
		ScreenServletRequest request = new ScreenServletRequest(httpServletRequest);

		User user = userInstance.get();
		if (user != null && user.getId() != null)
		{
			event.fire(new LogoffEvent(user));
			response.addCookie(CookieFactory.delete(SUBJECT_COOKIE));

			String logoutUri = authenticator.logoutUri(request);
			if (logoutUri != null)
				response.sendRedirect(logoutUri);
			else
				handlers.select(HTMLCommandHandler.class).get().handle(request, response, HTML);
		} else
			handlers.select(HTMLCommandHandler.class).get().handle(request, response, HTML);
	}
}
