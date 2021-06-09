package gate.base;

import gate.Progress;
import gate.entity.App;
import gate.entity.Org;
import gate.entity.User;
import javax.enterprise.inject.spi.CDI;
import gate.annotation.Current;

abstract class Base
{

	/**
	 * Returns information about the current user.
	 *
	 * @return an User object with information about the current user
	 */
	public User getUser()
	{
		if (Progress.getUser() != null)
			return Progress.getUser();

		return CDI.current().select(User.class, Current.QUALIFIER).get();
	}

	/**
	 * Returns information about the current organization.
	 *
	 * @return an Org object with information about the current organization
	 */
	public Org getOrg()
	{

		if (Progress.getOrg() != null)
			return Progress.getOrg();

		return CDI.current().select(Org.class, Current.QUALIFIER).get();
	}

	/**
	 * Returns information about the current application.
	 *
	 * @return an App object with information about the current application
	 */
	public App getApp()
	{
		if (Progress.getApp() != null)
			return Progress.getApp();

		return CDI.current().select(App.class, Current.QUALIFIER).get();
	}
}
