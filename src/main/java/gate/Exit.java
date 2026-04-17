package gate;

import gate.annotation.Current;
import gate.authenticator.Authenticator;
import gate.entity.User;
import gate.event.AppEvent;
import gate.event.LogoffEvent;
import gate.handler.HTMLCommandHandler;
import gate.http.ScreenServletRequest;
import gate.http.ScreenServletResponse;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(value = "/Exit")
public class Exit extends HttpServlet
{

	@Inject Event<AppEvent> event;

	@Inject HTMLCommandHandler handler;

	@Inject
	@Current
	Instance<User> userInstance;

	@Inject
	@Current
	Authenticator authenticator;

	private static final String HTML = "/views/Exit.html";

	@Override
	public void service(HttpServletRequest httpServletRequest,
	                    HttpServletResponse httpServletResponse) throws IOException
	{
		ScreenServletRequest request = new ScreenServletRequest(httpServletRequest);
		ScreenServletResponse response = new ScreenServletResponse(httpServletResponse);
		response.addHeader("Vary", "X-G-Fragment");

		response.revokeSessionCookie(request);

		User user = userInstance.get();
		if (user != null && user.getId() != null)
		{
			event.fire(new LogoffEvent(user));
			String logoutUri = authenticator.logoutUri(request);
			if (logoutUri != null)
				response.sendRedirect(logoutUri);
			else
				handler.handle(request, response, HTML);
		} else
			handler.handle(request, response, HTML);
	}
}