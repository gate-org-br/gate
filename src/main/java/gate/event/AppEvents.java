package gate.event;

import gate.entity.User;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.enterprise.event.Observes;
import javax.enterprise.event.ObservesAsync;
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

@ServerEndpoint(value = "/AppEvents",
	configurator = AppEvents.Configurator.class)
public class AppEvents
{

	private static final List<UserSession> sessions = new CopyOnWriteArrayList<>();

	@OnOpen
	public void open(Session session, EndpointConfig config)
	{
		sessions.add(new UserSession((User) config.getUserProperties().get(User.class.getName()), session));
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
			config.getUserProperties().put(User.class.getName(),
				((HttpSession) request.getHttpSession()).getAttribute(User.class.getName()));
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
