package gate.producer;

import gate.GateControl;
import gate.annotation.Current;
import gate.entity.User;
import gate.error.HierarchyException;
import gate.http.ScreenServletRequest;
import gate.security.Credentials;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

public class UserProducer
{
	@Current
	@Produces
	@RequestScoped
	@Named(value = "user")
	public User getUser(GateControl control,
	                    Credentials credentials,
	                    HttpServletRequest httpServletRequest)
			throws HierarchyException
	{
		if (httpServletRequest == null)
			return new User();
		if (httpServletRequest.getAttribute(User.class.getName())
				instanceof User user)
			return user;

		ScreenServletRequest request = new ScreenServletRequest(httpServletRequest);
		var auth = request.getAuthorization();
		if (auth == null)
			return new User();
		var token = auth.token();
		if (token == null)
			return new User();
		User user = control.select(credentials.parseToken(token).id());
		request.setAttribute(User.class.getName(), user);
		return user;
	}
}