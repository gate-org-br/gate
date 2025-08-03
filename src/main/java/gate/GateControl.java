package gate;

import gate.entity.User;
import gate.error.HierarchyException;
import gate.error.InvalidUsernameException;
import gate.sql.Link;
import gate.type.Hierarchy;
import gate.type.ID;
import gate.util.Toolkit;
import jakarta.enterprise.context.Dependent;

@Dependent
public class GateControl extends gate.base.Control
{

	public User select(ID id) throws InvalidUsernameException,
		HierarchyException
	{

		try (Link link = Link.of("Gate");
			GateDao dao = new GateDao(link))
		{
			User user = dao.select(id);

			if (user.isDisabled())
				throw new InvalidUsernameException();
			if (user.getRole().getId() == null)
				throw new InvalidUsernameException();

			var roles = dao.getRoles();
			Hierarchy.setup(roles);
			user.setRole(roles.stream().filter(user.getRole()::equals).findAny()
				.orElseThrow(() -> new HierarchyException("User role not found")));
			return user;
		}
	}

	public User select(String username) throws InvalidUsernameException,
		HierarchyException
	{
		if (Toolkit.isEmpty(username) || username.length() > 64)
			throw new InvalidUsernameException();

		try (Link link = Link.of("Gate");
			GateDao dao = new GateDao(link))
		{
			User user = dao.select(username);

			if (user.isDisabled())
				throw new InvalidUsernameException();
			if (user.getRole().getId() == null)
				throw new InvalidUsernameException();

			var roles = dao.getRoles();
			Hierarchy.setup(roles);
			user.setRole(roles.stream().filter(user.getRole()::equals).findAny()
				.orElseThrow(() -> new HierarchyException("User role not found")));
			return user;
		}
	}
}
