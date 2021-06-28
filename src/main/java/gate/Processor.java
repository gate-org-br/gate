package gate;

import java.io.IOException;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import org.slf4j.Logger;

@ApplicationScoped
@ServerEndpoint("/Progress/{process}")
public class Processor
{

	@Inject
	private Logger logger;

	@OnOpen
	public void open(@PathParam("process") int id, Session session)
	{
		try
		{
			Progress progress = Progress.INSTANCES.get(id);
			if (progress != null)
				progress.add(session);
			else
				session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT,
					"Tarefa inexistente"));
		} catch (IOException | RuntimeException ex)
		{
			logger.error(ex.getMessage(), ex);
		}
	}

	@OnClose
	public void close(@PathParam("process") int id, Session session, CloseReason reason)
	{
		Progress progress = Progress.INSTANCES.get(id);
		if (progress != null)
			progress.rem(session);
	}
}
