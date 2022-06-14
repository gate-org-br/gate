package gate;

import gate.base.Control;
import gate.base.Dao;
import gate.entity.User;
import gate.error.AppException;
import gate.event.LoginEvent;
import gate.event.LogoffEvent;
import gate.sql.condition.Condition;
import gate.sql.update.Update;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;
import javax.servlet.http.HttpSessionListener;

@ApplicationScoped
public class LoginEventListener implements HttpSessionListener
{

	@Inject
	private LoginListenerControl control;

	public void login(@ObservesAsync LoginEvent event)
	{
		try
		{
			control.update(event.getUser(), User.Status.ONLINE);
		} catch (AppException | RuntimeException ex)
		{
			Logger.getLogger(LoginEventListener.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void logoff(@ObservesAsync LogoffEvent event)
	{
		try
		{
			control.update(event.getUser(), User.Status.OFFLINE);
		} catch (AppException | RuntimeException ex)
		{
			Logger.getLogger(LoginEventListener.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Dependent
	private static class LoginListenerControl extends Control
	{

		public void update(User user, User.Status status)
			throws AppException
		{
			try ( LoginListenerDao dao = new LoginListenerDao())
			{
				dao.update(user, status);
			}
		}

		private static class LoginListenerDao extends Dao
		{

			public LoginListenerDao()
			{
				super("Gate");
			}

			public void update(User user, User.Status status) throws AppException
			{
				Update.table("Uzer")
					.set("status", status)
					.where(Condition.of("id")
						.eq(user.getId()).and("status").ne(User.Status.INACTIVE))
					.build()
					.connect(getLink())
					.execute();
			}
		}

	}
}
