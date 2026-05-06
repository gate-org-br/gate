package gate.event;

import gate.entity.User;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class EventClients
{
	private final Set<EventClient> clients =
			Collections.newSetFromMap(new ConcurrentHashMap<>());

	public void subscribe(EventClient client) {clients.add(client);}

	public void unsubscribe(EventClient client)
	{
		client.close();
		clients.remove(client);
	}

	void dispatch(Predicate<User> predicate, String event, String message)
	{
		Iterator<EventClient> it = clients.iterator();
		while (it.hasNext())
		{
			EventClient client = it.next();
			if (!client.dispatch(predicate, event, message))
			{
				it.remove();
				client.close();
			}
		}
	}

	public void dispatch(String message) {dispatch(e -> true, "message", message);}

	public void dispatch(String event, String message) {dispatch(e -> true, event, message);}

	public void dispatch(Predicate<User> predicate, String message) {dispatch(predicate, "message", message);}

	public synchronized void close()
	{
		for (EventClient client : clients)
			client.close();
	}
}