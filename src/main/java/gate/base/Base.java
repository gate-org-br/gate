package gate.base;

import gate.annotation.Current;
import gate.entity.App;
import gate.entity.Org;
import gate.entity.User;
import javax.enterprise.inject.spi.CDI;

abstract class Base
{

	private Org org;
	private App app;
	private User user;

	/**
	 * Returns information about the current user.
	 *
	 * @return an User object with information about the current user
	 */
	public User getUser()
	{
		if (user != null)
			return user;

		return user = CDI.current().select(User.class, Current.LITERAL).get();
	}

	/**
	 * Returns information about the current organization.
	 *
	 * @return an Org object with information about the current organization
	 */
	public Org getOrg()
	{
		if (org != null)
			return org;

		return org = CDI.current().select(Org.class, Current.LITERAL).get();
	}

	/**
	 * Returns information about the current application.
	 *
	 * @return an App object with information about the current application
	 */
	public App getApp()
	{
		if (app != null)
			return app;

		return app = CDI.current().select(App.class, Current.LITERAL).get();
	}
}
