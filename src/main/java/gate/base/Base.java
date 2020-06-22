package gate.base;

import gate.Progress;
import gate.entity.App;
import gate.entity.Org;
import gate.entity.User;
import gate.producer.AppProducer;
import gate.producer.OrgProducer;
import gate.producer.UserProducer;
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
		if (user == null)
			if (Progress.getUser() != null)
				user = Progress.getUser();
			else
				user = CDI.current().select(UserProducer.class).get().getUser();
		return user;
	}

	/**
	 * Returns information about the current organization.
	 *
	 * @return an Org object with information about the current organization
	 */
	public Org getOrg()
	{
		if (org == null)
			if (Progress.getOrg() != null)
				org = Progress.getOrg();
			else
				org = CDI.current().select(OrgProducer.class).get().produce();
		return org;
	}

	/**
	 * Returns information about the current application.
	 *
	 * @return an App object with information about the current application
	 */
	public App getApp()
	{
		if (app == null)
			if (Progress.getApp() != null)
				app = Progress.getApp();
			else
				app = CDI.current().select(AppProducer.class).get().produce();
		return app;
	}

}
