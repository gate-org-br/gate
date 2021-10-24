package gate;

import gate.entity.User;
import gate.event.LogoffEvent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

@WebListener
public class LogoffListener implements HttpSessionListener
{

	@Inject
	private Event<LogoffEvent> event;

	@Override
	public void sessionDestroyed(HttpSessionEvent se)
	{
		User user = (User) se.getSession().getAttribute(User.class.getName());
		if (user != null)
			event.fireAsync(new LogoffEvent(user));
	}
}
