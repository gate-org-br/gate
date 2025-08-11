package gate;

import gate.annotation.Current;
import gate.catcher.UnauthorizedExceptionCatcher;
import gate.entity.User;
import gate.error.UnauthorizedException;
import gate.event.AppEvent;
import gate.http.ScreenServletRequest;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.inject.Inject;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@WebServlet(value = "/SSE", asyncSupported = true)
public class SSEServlet extends HttpServlet
{

	@Inject
	@Current
	User user;

	@Inject
	UnauthorizedExceptionCatcher catcher;

	private static final List<Client> clients = new CopyOnWriteArrayList<>();
	private static final ScheduledExecutorService PINGER = Executors.newSingleThreadScheduledExecutor();

	@Override
	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);
		PINGER.scheduleAtFixedRate(() -> clients.forEach(e -> e.ping()), 30, 30, TimeUnit.SECONDS);
	}

	@Override
	public void destroy()
	{
		PINGER.shutdownNow();
		clients.forEach(Client::close);
		clients.clear();
		super.destroy();
	}

	@Override
	protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse response) throws ServletException, IOException
	{
		ScreenServletRequest request = new ScreenServletRequest(httpServletRequest);

		try
		{
			if (user == null || user.getId() == null)
				throw new UnauthorizedException();

			response.setHeader("Connection", "keep-alive");
			response.setHeader("X-Accel-Buffering", "no");
			response.setContentType("text/event-stream; charset=UTF-8");
			response.setHeader("Cache-Control", "no-cache, no-transform");

			AsyncContext context = request.startAsync();
			context.setTimeout(TimeUnit.HOURS.toMillis(1));

			Client client = new Client(user.unwrap(), context);
			clients.add(client);

			client.ping();

			context.addListener(new AsyncListener()
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
		} catch (UnauthorizedException ex)
		{
			catcher.catches(request, response, ex);
		}
	}

	public void onAPPEvent(@Observes AppEvent event)
	{
		clients.forEach(e -> e.send(event));
	}

	public void onAPPEventAsync(@ObservesAsync AppEvent event)
	{
		clients.forEach(e -> e.send(event));
	}

	private static class Client
	{

		private final User subject;
		private ServletOutputStream out;
		private final AsyncContext asyncContext;

		public Client(User user, AsyncContext asyncContext)
		{
			this.subject = user;
			this.asyncContext = asyncContext;
		}

		public synchronized void ping()
		{
			try
			{
				if (out == null)
					out = asyncContext.getResponse().getOutputStream();

				out.print("event: ping\n");
				out.print("data: ok\n");
				out.print("\n");
				out.flush();
				asyncContext.getResponse().flushBuffer();
			} catch (IOException | RuntimeException ex)
			{
				try
				{
					asyncContext.complete();
				} catch (IllegalStateException ignored)
				{
				}
			}

		}

		public synchronized void send(AppEvent event)
		{
			if (event.checkAccess(subject))
			{
				try
				{
					if (out == null)
						out = asyncContext.getResponse().getOutputStream();

					var data = Base64.getEncoder()
						.encodeToString(event.toString()
							.getBytes(StandardCharsets.UTF_8));

					out.print("event: message\n");
					out.print("data: " + data + "\n");
					out.print("\n");
					out.flush();
					asyncContext.getResponse().flushBuffer();
				} catch (IOException | RuntimeException ex)
				{
					try
					{
						asyncContext.complete();
					} catch (IllegalStateException ignored)
					{
					}
				}
			}
		}

		public void close()
		{
			try
			{
				if (out != null)
					out.close();
			} catch (IOException ignored)
			{
			}
		}
	}
}
