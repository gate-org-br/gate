package gate.event;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.enterprise.event.Observes;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@Startup
@Singleton
@ServerEndpoint("/AppEvents")
@TransactionManagement(TransactionManagementType.BEAN)
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
}
