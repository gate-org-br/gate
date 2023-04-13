package gate;

import gate.annotation.Converter;
import gate.annotation.Name;
import gate.converter.EnumStringConverter;
import gate.entity.User;
import gate.event.AppEvent;
import gate.event.LoginEvent;
import gate.event.LogoffEvent;
import gate.type.ID;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

@WebListener
@ApplicationScoped
public class Status implements HttpSessionListener
{

	@Inject
	private Event<AppEvent> event;

	private final List<User> online
		= new CopyOnWriteArrayList<>();

	@Override
	public void sessionCreated(HttpSessionEvent se)
	{
		User user = (User) se.getSession().getAttribute(User.class.getName());
		if (user != null)
		{
			online.add(user);
			event.fireAsync(new LoginEvent(user));
		}
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent se)
	{
		User user = (User) se.getSession().getAttribute(User.class.getName());
		if (user != null)
		{
			online.remove(user);
			event.fireAsync(new LogoffEvent(user));
		}
	}

	public Values get(ID id)
	{
		return online.stream()
			.anyMatch(e -> e.getId().equals(id))
			? Values.ONLINE
			: Values.OFFLINE;
	}

	public Values get(User user)
	{
		return online.contains(user)
			? Values.ONLINE
			: Values.OFFLINE;
	}

	@Converter(EnumStringConverter.class)
	public enum Values
	{
		@Name("Offline")
		OFFLINE,
		@Name("Online")
		ONLINE;

		@Override
		public String toString()
		{
			return Name.Extractor.extract(this).orElseThrow();
		}
	}

}
