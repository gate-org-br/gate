package gate;

import gate.event.AppEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.ObservesAsync;

import java.util.Collections;
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

    void heartbeat()
    {
        clients.removeIf(e -> !e.heartbeat());
    }

    void onAPPEvent(@Observes AppEvent event)
    {
        clients.removeIf(client ->
        {
            if (client.send(event))
                return false;

            client.close();
            return true;
        });
    }

    void onAPPEventAsync(@ObservesAsync AppEvent event)
    {
        onAPPEvent(event);
    }
}
