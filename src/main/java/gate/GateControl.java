package gate;

import gate.annotation.DataSource;
import gate.entity.User;
import gate.error.AppException;
import gate.error.AuthenticationException;
import gate.error.HierarchyException;
import gate.error.InvalidUsernameException;
import gate.http.BearerAuthorization;
import gate.http.ScreenServletRequest;
import gate.security.Credentials;
import gate.sql.Link;
import gate.sql.LinkSource;
import gate.type.Hierarchy;
import gate.util.SystemProperty;
import gate.util.Toolkit;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class GateControl extends gate.base.Control
{

	@Inject
	@DataSource("Gate")
	LinkSource linksource;

	public User select(String username) throws InvalidUsernameException,
			HierarchyException
	{
		if (Toolkit.isEmpty(username) || username.length() > 64)
			throw new InvalidUsernameException();

		try (Link link = linksource.getLink();
				GateDao dao = new GateDao(link))
		{
			User user = dao.select(username);

			if (user.isDisabled())
				throw new InvalidUsernameException();
			if (user.getRole().getId() == null)
				throw new InvalidUsernameException();

			var roles = dao.getRoles();
			Hierarchy.setup(roles);
			user.setRole(roles.stream().filter(e -> e.equals(user.getRole())).findAny()
					.orElseThrow(() -> new HierarchyException("User role not found")));
			return user;
		}
	}

	public void update(User user) throws AppException
	{
		try (Link link = linksource.getLink();
				GateDao dao = new GateDao(link))
		{
			dao.update(user);
		}
	}

	public void update(User user, String password) throws AppException
	{
		try (Link link = linksource.getLink();
				GateDao dao = new GateDao(link))
		{
			dao.update(user, password);
		}
	}

	public void authenticate(ScreenServletRequest request)
			throws AuthenticationException,
			InvalidUsernameException, HierarchyException
	{

		String developer = SystemProperty.get("gate.developer").orElse(null);
		if (request.getAuthorization().orElse(null) instanceof BearerAuthorization authorization)
			request.setUser(Credentials.of(authorization.token()));
		else if (request.getSession(false) != null
				&& request.getSession().getAttribute(User.class.getName()) != null)
			request.setUser((User) request.getSession().getAttribute(User.class.getName()));
		else if (developer != null)
			request.setUser(select(developer));
	}
}
