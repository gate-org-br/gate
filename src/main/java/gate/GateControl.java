package gate;

import gate.annotation.DataSource;
import gate.entity.User;
import gate.error.AppException;
import gate.error.HierarchyException;
import gate.error.InvalidUsernameException;
import gate.sql.Link;
import gate.sql.LinkSource;
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

			user.setRole(dao.getRoles().stream().filter(e -> e.equals(user.getRole())).findAny()
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

}
