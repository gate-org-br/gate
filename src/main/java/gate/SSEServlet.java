package gate;

import gate.annotation.Current;
import gate.catcher.UnauthorizedExceptionCatcher;
import gate.entity.User;
import gate.error.UnauthorizedException;
import gate.event.AppEvent;
import gate.http.ScreenServletRequest;
import jakarta.enterprise.context.RequestScoped;
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
import java.util.concurrent.TimeUnit;

@WebServlet(value = "/SSE", asyncSupported = true)
public class SSEServlet extends HttpServlet
{
	@Inject
	UnauthorizedExceptionCatcher catcher;

	@Inject
	@Current
	@RequestScoped
	User user;

	private static final List<Client> clients = new CopyOnWriteArrayList<>();


	@Override
	protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse response) throws ServletException, IOException
	{
		ScreenServletRequest request = new ScreenServletRequest(httpServletRequest);

		try
		{
			if (user == null || user.getId() == null)
				throw new UnauthorizedException();

			response.setCharacterEncoding("UTF-8");
			response.setContentType("text/event-stream");

			AsyncContext context = request.startAsync();
			context.setTimeout(TimeUnit.HOURS.toMillis(1));

			Client client = new Client(user.unwrap(), context);

			clients.stream()
					.filter(e -> e.subject.equals(user))
					.toList()
					.forEach(e -> e.asyncContext.complete());
			clients.add(client);

			context.addListener(new AsyncListener()
			{
				@Override
				public void onStartAsync(AsyncEvent event)
				{
				}

				@Override
				public void onComplete(AsyncEvent event)
				{
					client.close();
					clients.remove(client);
				}

				@Override
				public void onError(AsyncEvent event)
				{
					client.close();
					clients.remove(client);
				}

				@Override
				public void onTimeout(AsyncEvent event)
				{
					client.close();
					clients.remove(client);
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

					out.println("event: message");
					out.println("data: " + data);
					out.println();
					out.flush();
					asyncContext.getResponse().flushBuffer();
				} catch (IOException | RuntimeException ex)
				{
					asyncContext.complete();
				}
			}
		}

		public void close()
		{
			try
			{
				out.close();
			} catch (Exception ignored)
			{
			}
		}
	}
}