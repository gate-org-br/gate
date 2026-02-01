package gate;

import gate.annotation.Current;
import gate.authenticator.Authenticator;
import gate.entity.User;
import gate.event.AppEvent;
import gate.event.LogoffEvent;
import gate.handler.HTMLCommandHandler;
import gate.handler.Handler;
import gate.http.ScreenServletRequest;
import gate.http.ScreenServletResponse;
import gate.i18n.I18N;
import gate.type.ID;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

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
	GateControl control;

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
			HttpServletResponse httpServletResponse)
			throws ServletException, IOException
	{
		httpServletResponse.addHeader("Vary", "X-G-Fragment");
		ScreenServletRequest request = new ScreenServletRequest(httpServletRequest);
		ScreenServletResponse response = new ScreenServletResponse(httpServletResponse);

		User user = userInstance.get();
		if (user == null || user.getId() == null)
		{
			handlers.select(HTMLCommandHandler.class)
					.get().handle(request, response, HTML);
			return;
		}

		ID subject = (ID) request.getParameter(ID.class, "user");
		if (subject == null || subject.equals(user.getId()))
		{
			event.fire(new LogoffEvent(user));
			response.deleteSubjectCookie();

			if (subject != null)
				control.update(user, LocalDateTime.now(ZoneOffset.UTC));

			String logoutUri = authenticator.logoutUri(request);
			if (logoutUri != null)
			{
				response.sendRedirect(logoutUri);
				return;
			}

			response.send(I18N.get("auth.session.revoked"));
			return;
		}

		if (!user.isSuperUser())
		{
			response.sendError(HttpServletResponse.SC_FORBIDDEN, I18N.get("auth.logout.other.forbidden"));
			return;
		}

		control.update(new User().setId(subject), LocalDateTime.now(ZoneOffset.UTC));
		response.send(I18N.get("auth.session.revoked.other"));
	}
}
