package gate.producer;

import gate.GateControl;
import gate.annotation.Current;
import gate.entity.User;
import gate.security.Credentials;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

public class UserProducer
{
	@Inject
	GateControl control;

	@Current
	@Produces
	@RequestScoped
	@Named(value = "user")
	public User getUser(HttpServletRequest httpServletRequest)
	{
		if (httpServletRequest.getAttribute(User.class.getName()) instanceof User user)
			return user;

		var subject = (Credentials.Subject) httpServletRequest
				.getAttribute(Credentials.Subject.class.getName());
		if (subject == null)
			return new User();

		User user = control.select(subject.id());

		httpServletRequest.setAttribute(User.class.getName(), user);
		return user;
	}
}