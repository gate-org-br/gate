package gate.event;

import gate.entity.User;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.HandshakeResponse;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.ServerEndpointConfig;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;

@ApplicationScoped
@ServerEndpoint(value = "/AppEvents",
	configurator = AppEvents.Configurator.class)
public class AppEvents
{

	@Inject
	@ConfigProperty(name = "gate.events")
	Optional<String> events;

	@Inject
	private Logger logger;

	private static final List<UserSession> sessions = new CopyOnWriteArrayList<>();

	@OnOpen
	public void open(Session session, EndpointConfig config)
	{
		try
		{
			if ("false".equalsIgnoreCase(events.orElse("true")))
				session.close();
			sessions.add(new UserSession((User) config.getUserProperties().get(User.class.getName()), session));
		} catch (IOException ex)
		{
			logger.error(ex.getMessage(), ex);
		}
	}

	@OnClose
	public void close(Session session, CloseReason reason)
	{
		sessions.removeIf(e -> e.session.equals(session));
	}

	public void onEvento(@Observes AppEvent event)
	{
		sessions.stream()
			.filter(e -> event.checkAccess(e.user))
			.forEach(e -> e.session.getAsyncRemote().sendText(event.toString()));
	}

	public void onEventoAsync(@ObservesAsync AppEvent event)
	{

		sessions.stream()
			.filter(e -> event.checkAccess(e.user))
			.forEach(e -> e.session.getAsyncRemote().sendText(event.toString()));
	}

	public static class Configurator extends ServerEndpointConfig.Configurator
	{

		@Override
		public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response)
		{
			HttpSession session = (HttpSession) request.getHttpSession();
			User user = session != null ? (User) session.getAttribute(User.class.getName()) : null;
			config.getUserProperties().put(User.class.getName(), user);
		}
	}

	public static class UserSession
	{

		private User user;
		private Session session;

		public UserSession(User user, Session session)
		{
			this.user = user;
			this.session = session;
		}

	}
}
