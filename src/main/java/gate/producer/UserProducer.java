package gate.producer;

import gate.GateContext;
import gate.GateControl;
import gate.annotation.Current;
import gate.entity.User;
import gate.security.Credentials;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

@ApplicationScoped
public class UserProducer
{
	@Inject
	GateControl control;

	@Inject
	Instance<HttpServletRequest> requestInstance;

	@Current
	@Produces
	@Dependent
	@Named(value = "user")
	public User getUser()
	{
		var context = GateContext.get();
		if (context != null)
			return context.user();

		HttpServletRequest request;
		Credentials.Subject subject;

		try
		{
			request = requestInstance.get();
			if (request.getAttribute(User.class.getName()) instanceof User user)
				return user;
			subject = (Credentials.Subject) request
					.getAttribute(Credentials.Subject.class.getName());
		} catch (RuntimeException ex) {return new User();}

		User user = subject != null ? control.select(subject.id()) : new User();
		try {request.setAttribute(User.class.getName(), user);} catch (Exception ignored) {}
		return user;
	}
}