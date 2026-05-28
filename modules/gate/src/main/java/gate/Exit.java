package gate;

import gate.adapter.handler.HTMLCommandHandler;
import gate.adapter.handler.Handler;
import gate.annotation.Current;
import gate.catalog.SessionCatalog;
import gate.entity.User;
import gate.event.AppEvent;
import gate.event.LogoffEvent;
import gate.http.ScreenServletRequest;
import gate.http.ScreenServletResponse;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Terminates authentication sessions.
 *
 * <p>
 * Behavior:
 * <ul>
 * <li>If the {@code user} parameter is not provided, only the current session is terminated.</li>
 * <li>If {@code user} matches the authenticated user, all sessions of the same user are terminated.</li>
 * <li>If {@code user} refers to a different user, administrative privileges are required and all sessions of the
 * specified user are terminated.</li>
 * </ul>
 *
 * <p>
 * A global session termination updates the user's {@code activity} timestamp, causing all previously issued tokens to
 * become invalid.
 *
 * <p>
 * When supported by the authenticator, an external logout endpoint (such as OIDC logout) may also be triggered.
 */
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
	SessionCatalog sessionCatalog;

	private static final String HTML = "/views/Exit.html";

	@Override
	public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
	{
		httpServletResponse.addHeader("Vary", "X-G-Fragment");
		ScreenServletRequest request = new ScreenServletRequest(httpServletRequest);
		ScreenServletResponse response = new ScreenServletResponse(httpServletResponse);

		User user = userInstance.get();
		if (user != null && user.getId() != null)
		{
			response.revokeSessionCookie(request);
			sessionCatalog.revoke(request.getAuthentication().token());
			event.fire(new LogoffEvent(user));
		} else
			handlers.select(HTMLCommandHandler.class)
					.get().handle(request, response, HTML);
	}
}