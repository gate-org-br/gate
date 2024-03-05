package gate;

import gate.catcher.UnauthorizedExceptionCatcher;
import gate.entity.User;
import gate.error.InvalidCredentialsException;
import gate.error.UnauthorizedException;
import gate.event.AppEvent;
import gate.io.Credentials;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.enterprise.event.Observes;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;
import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;

@WebServlet(value = "/SSE", asyncSupported = true)
public class SSEServlet extends HttpServlet
{

	@Inject
	UnauthorizedExceptionCatcher catcher;

	@Inject
	Logger logger;
	private static final Map<AsyncContext, Client> clients = new ConcurrentHashMap<>();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		try
		{
			User user = Credentials.isPresent(request)
				? Credentials.of(request).orElseThrow(UnauthorizedException::new)
				: (User) request.getSession().getAttribute(User.class.getName());

			if (user == null)
				throw new UnauthorizedException();

			response.setCharacterEncoding("UTF-8");
			response.setContentType("text/event-stream");

			AsyncContext context = request.startAsync();
			context.setTimeout(0);
			clients.put(context, new Client(user, context));
			context.addListener(new AsyncListener()
			{
				@Override
				public void onStartAsync(AsyncEvent event) throws IOException
				{
				}

				@Override
				public void onComplete(AsyncEvent event) throws IOException
				{
					clients.remove(event.getAsyncContext());
				}

				@Override
				public void onError(AsyncEvent event) throws IOException
				{
					onComplete(event);
				}

				@Override
				public void onTimeout(AsyncEvent event) throws IOException
				{
					onComplete(event);
				}
			});
		} catch (InvalidCredentialsException ex)
		{
			catcher.catches(request, response,
				new UnauthorizedException());
		} catch (UnauthorizedException ex)
		{
			catcher.catches(request, response, ex);
		}
	}

	public void onAPPEvent(@Observes AppEvent event)
	{
		for (Client client : clients.values())
			client.send(event);
	}

	public void onAPPEventAscync(@ObservesAsync AppEvent event)
	{
		for (Client client : clients.values())
			client.send(event);
	}

	private class Client
	{

		private final User user;
		private PrintWriter writer;
		private final AsyncContext asyncContext;

		public Client(User user, AsyncContext asyncContext)
		{
			this.user = user;
			this.asyncContext = asyncContext;
		}

		public synchronized void send(AppEvent event)
		{
			if (event.checkAccess(user))
			{
				try
				{
					if (writer == null)
						writer = asyncContext.getResponse().getWriter();

					writer.write("event: message\n");
					writer.write("data: " + Base64.getEncoder()
						.encodeToString(event.toString().getBytes()) + "\n\n");
					writer.flush();
				} catch (IOException ex)
				{
					this.close();
				}
			}
		}

		public void close()
		{
			try
			{
				writer.close();
			} catch (Exception ex)
			{
				logger.trace(ex.getMessage(), ex);
			} finally
			{
				asyncContext.complete();
			}
		}
	}
}
