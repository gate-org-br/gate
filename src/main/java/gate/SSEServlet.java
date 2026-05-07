package gate;

import gate.annotation.Current;
import gate.catcher.UnauthorizedExceptionCatcher;
import gate.entity.User;
import gate.error.UnauthorizedException;
import gate.event.AppEvent;
import gate.event.EventClient;
import gate.event.EventClients;
import gate.http.ScreenServletRequest;
import gate.http.ScreenServletResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.enterprise.inject.Instance;
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
import java.util.concurrent.TimeUnit;

@WebServlet(value = "/SSE", asyncSupported = true)
public class SSEServlet extends HttpServlet
{
	@Inject
	@Current
	@RequestScoped
	Instance<User> userInstance;

	@Inject
	EventListener eventListener;

	@Inject
	UnauthorizedExceptionCatcher catcher;

	@Override
	protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
			throws ServletException, IOException
	{
		httpServletResponse.addHeader("Cache-Control", "no-cache");
		ScreenServletResponse response = new ScreenServletResponse(httpServletResponse);
		ScreenServletRequest request = new ScreenServletRequest(httpServletRequest);

		try
		{
			User user = userInstance.get();
			if (user == null || user.getId() == null)
				throw new UnauthorizedException();

			response.setCharacterEncoding("UTF-8");
			response.setContentType("text/event-stream");

			AsyncContext context = request.startAsync();
			context.setTimeout(TimeUnit.HOURS.toMillis(1));

			EventClient client = new EventClient(user, context);
			eventListener.subscribe(client);

			context.addListener(new AsyncListener()
			{
				@Override
				public void onStartAsync(AsyncEvent event) {}

				@Override
				public void onComplete(AsyncEvent event)
				{
					eventListener.unsubscribe(client);
				}

				@Override
				public void onError(AsyncEvent event)
				{
					onComplete(event);
				}

				@Override
				public void onTimeout(AsyncEvent event)
				{
					onComplete(event);
				}
			});
		} catch (UnauthorizedException ex)
		{
			catcher.catches(request, response, ex);
		}
	}

	@ApplicationScoped
	public static class EventListener extends EventClients
	{
		void onEvent(@Observes AppEvent event)
		{
			dispatch(event::checkAccess, event.toJsonObject());
			clients.stream().filter(e -> !e.isConnected()).forEach(EventClient::close);
			clients.removeIf(e -> !e.isClosed());
		}

		void onEventAsync(@ObservesAsync AppEvent event) {onEvent(event);}
	}
}