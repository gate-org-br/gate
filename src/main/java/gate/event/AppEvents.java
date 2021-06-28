package gate.event;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.ObservesAsync;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ApplicationScoped
@ServerEndpoint("/AppEvents")
public class AppEvents
{

	private final List<Session> sessions = new CopyOnWriteArrayList<>();

	@OnOpen
	public void open(Session session)
	{
		sessions.add(session);
	}

	@OnClose
	public void close(Session session, CloseReason reason)
	{
		sessions.remove(session);
	}

	public void onEvento(@Observes AppEvent event)
	{
		sessions.stream().forEach(e -> e.getAsyncRemote().sendText(event.toString()));
	}

	public void onEventoAsync(@ObservesAsync AppEvent event)
	{
		sessions.stream().forEach(e -> e.getAsyncRemote().sendText(event.toString()));
	}
}
