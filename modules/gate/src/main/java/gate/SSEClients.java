package gate;

import gate.event.AppEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.ObservesAsync;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class SSEClients
{
	private final Set<SSEClient> clients =
			Collections.newSetFromMap(new ConcurrentHashMap<>());

	void add(SSEClient client)
	{
		clients.add(client);
	}

	void remove(SSEClient client)
	{
		client.close();
		clients.remove(client);
	}

	void onAPPEvent(@Observes AppEvent event)
	{
		Iterator<SSEClient> it = clients.iterator();
		while (it.hasNext())
		{
			SSEClient client = it.next();
			if (!client.send(event))
			{
				it.remove();
				client.close();
			}
		}
	}

	void onAPPEventAsync(@ObservesAsync AppEvent event)
	{
		onAPPEvent(event);
	}
}
