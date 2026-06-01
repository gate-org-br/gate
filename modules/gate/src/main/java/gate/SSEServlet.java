package gate;

import gate.adapter.catcher.UnauthorizedExceptionCatcher;
import gate.annotation.Current;
import gate.entity.User;
import gate.error.UnauthorizedException;
import gate.event.AppEvent;
import gate.event.EventClients;
import gate.http.ScreenServletRequest;
import gate.http.ScreenServletResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(value = "/SSE", asyncSupported = true)
public class SSEServlet extends HttpServlet
{
	@Inject
	EventListener listener;

	@Inject
	@Current
	Instance<User> userInstance;

	@Inject
	UnauthorizedExceptionCatcher catcher;

	@Override
	protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
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
			var context = request.startAsync();
			context.setTimeout(0);
			listener.subscribe(user, context);
		} catch (UnauthorizedException ex)
		{
			catcher.catches(request, response, ex);
		}
	}

	@ApplicationScoped
	public static class EventListener
	{
		EventClients clients = new EventClients();

		void subscribe(User user, AsyncContext context) {clients.subscribe(context, user);}

		void onEventAsync(@ObservesAsync AppEvent event) {onEvent(event);}

		void onEvent(@Observes AppEvent event) {clients.dispatch(event::checkAccess, event.toJsonObject());}
	}
}