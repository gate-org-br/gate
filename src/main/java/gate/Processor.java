package gate;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Startup;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Singleton;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

@Startup
@Singleton
@ServerEndpoint("/Progress/{process}")
@TransactionManagement(TransactionManagementType.BEAN)
public class Processor
{

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
			Logger.getLogger(Processor.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
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
