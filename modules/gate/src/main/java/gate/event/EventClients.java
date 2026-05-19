package gate.event;

import gate.entity.User;
import gate.lang.json.JsonObject;
import jakarta.servlet.AsyncContext;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class EventClients
{
	protected final Set<EventClient> clients = Collections.newSetFromMap(new ConcurrentHashMap<>());

	public void subscribe(AsyncContext context, User user) {clients.add(new EventClient(context, user));}

	void dispatch(Predicate<User> predicate, String event, JsonObject message)
	{
		clients.forEach(client ->
		{
			if (!client.dispatch(predicate, event, message))
			{
				client.close();
				clients.remove(client);
			}
		});
	}

	public void dispatch(JsonObject message) {dispatch(e -> true, "message", message);}
	public void dispatch(String event, JsonObject message) {dispatch(e -> true, event, message);}
	public void dispatch(Predicate<User> predicate, JsonObject message) {dispatch(predicate, "message", message);}

	public synchronized void close()
	{
		for (EventClient client : clients)
			client.close();
		clients.clear();
	}
}