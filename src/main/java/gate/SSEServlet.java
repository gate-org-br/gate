package gate;

import gate.catcher.UnauthorizedExceptionCatcher;
import gate.entity.User;
import gate.error.AuthenticationException;
import gate.error.HierarchyException;
import gate.error.InvalidCredentialsException;
import gate.error.InvalidUsernameException;
import gate.error.UnauthorizedException;
import gate.event.AppEvent;
import gate.http.ScreenServletRequest;
import gate.io.Credentials;
import gate.util.SystemProperty;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.inject.Inject;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.AsyncEvent;
import jakarta.servlet.AsyncListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Logger;

@WebServlet(value = "/SSE", asyncSupported = true)
public class SSEServlet extends HttpServlet
{

	@Inject
	GateControl control;

	private final String developer
			= SystemProperty.get("gate.developer").orElse(null);

	@Inject
	UnauthorizedExceptionCatcher catcher;

	@Inject
	Logger logger;
	private static final List<Client> clients = new CopyOnWriteArrayList<>();

	private User getUser(ScreenServletRequest request)
			throws UnauthorizedException, AuthenticationException,
			InvalidUsernameException, HierarchyException
	{
		var token = request.getBearerAuthorization()
				.map(e -> e.token())
				.orElse(null);
		if (token != null)
			return Credentials.of(token);

		var session = request.getSession(false);
		if (session != null)
			return (User) session.getAttribute(User.class.getName());

		if (developer != null)
			return control.select(developer);

		throw new UnauthorizedException();
	}

	@Override
	protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse response) throws ServletException, IOException
	{
		ScreenServletRequest request = new ScreenServletRequest(httpServletRequest);

		try
		{
			User user = getUser(request);

			response.setCharacterEncoding("UTF-8");
			response.setContentType("text/event-stream");

			AsyncContext context = request.startAsync();
			context.setTimeout(0);

			Client client = new Client(user, context);
			clients.add(client);

			context.addListener(new AsyncListener()
			{
				@Override
				public void onStartAsync(AsyncEvent event) throws IOException
				{
				}

				@Override
				public void onComplete(AsyncEvent event) throws IOException
				{
					clients.remove(client);
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
		} catch (HierarchyException
				| UnauthorizedException
				| AuthenticationException ex)
		{
			catcher.catches(request, response, ex);
		}
	}

	public void onAPPEvent(@Observes
			@ObservesAsync AppEvent event)
	{
		for (Client client : clients)
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
				} catch (IOException | RuntimeException ex)
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
