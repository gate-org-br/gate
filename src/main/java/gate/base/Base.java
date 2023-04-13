package gate.base;

import gate.annotation.Current;
import gate.entity.App;
import gate.entity.Org;
import gate.entity.User;
import javax.enterprise.inject.spi.CDI;

abstract class Base
{

	/**
	 * Returns information about the current user.
	 *
	 * @return an User object with information about the current user
	 */
	public User getUser()
	{
		return CDI.current().select(User.class, Current.LITERAL).get();
	}

	/**
	 * Returns information about the current organization.
	 *
	 * @return an Org object with information about the current organization
	 */
	public Org getOrg()
	{
		return CDI.current().select(Org.class, Current.LITERAL).get();
	}

	/**
	 * Returns information about the current application.
	 *
	 * @return an App object with information about the current application
	 */
	public App getApp()
	{
		return CDI.current().select(App.class, Current.LITERAL).get();
	}
}
