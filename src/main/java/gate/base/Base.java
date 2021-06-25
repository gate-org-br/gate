package gate.base;

import gate.Progress;
import gate.entity.App;
import gate.entity.Org;
import gate.entity.User;
import javax.enterprise.inject.spi.CDI;
import gate.annotation.Current;

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

		if (Progress.getUser() != null)
			return user = Progress.getUser();

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

		if (Progress.getOrg() != null)
			return org = Progress.getOrg();

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

		if (Progress.getApp() != null)
			return app = Progress.getApp();

		return app = CDI.current().select(App.class, Current.LITERAL).get();
	}
}
