package gate.sse;

import gate.entity.User;
import gate.event.AppEvent;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.AsyncEvent;
import jakarta.servlet.AsyncListener;
import jakarta.servlet.ServletOutputStream;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class SSEClientManager
{

	private ScheduledExecutorService pinger;
	private final List<Client> clients = new CopyOnWriteArrayList<>();

	@PostConstruct
	public void init()
	{
		pinger = Executors.newSingleThreadScheduledExecutor();
		pinger.scheduleAtFixedRate(() -> clients.forEach(Client::ping), 30, 30, TimeUnit.SECONDS);
	}

	@PreDestroy
	public void destroy()
	{
		if (pinger != null && !pinger.isShutdown())
			pinger.shutdownNow();
		clients.forEach(Client::close);
		clients.clear();
	}

	public void addClient(User user, AsyncContext asyncContext)
	{
		Client client = new Client(user, asyncContext);
		clients.add(client);

		asyncContext.addListener(new AsyncListener()
		{
			private void cleanup()
			{
				client.close();
				clients.remove(client);
			}

			@Override
			public void onComplete(AsyncEvent e)
			{
				cleanup();
			}

			@Override
			public void onError(AsyncEvent e)
			{
				cleanup();
			}

			@Override
			public void onTimeout(AsyncEvent e)
			{
				cleanup();
			}

			@Override
			public void onStartAsync(AsyncEvent e)
			{
			}
		});

		client.ping();
	}

	public void removeClient(Client client)
	{
		clients.remove(client);
	}

	public void onAppEvent(@Observes AppEvent event)
	{
		clients.forEach(client -> client.send(event));
	}

	public void onAppEventAsync(@ObservesAsync AppEvent event)
	{
		clients.forEach(client -> client.send(event));
	}

}
