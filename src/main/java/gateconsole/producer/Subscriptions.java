package gateconsole.producer;

import gate.entity.User;
import gateconsole.contol.UserControl;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

@RequestScoped
public class Subscriptions
{

	@Inject
	private UserControl control;

	private List<User> subscriptions;

	@Produces
	@Named("subscriptions")
	public List<User> produce()
	{
		if (subscriptions == null)
			subscriptions = control.getSubscriptions();
		return subscriptions;
	}
}
