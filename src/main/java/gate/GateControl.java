package gate;

import gate.entity.User;
import gate.error.HierarchyException;
import gate.error.InvalidUsernamePasswordException;
import gate.security.hash.BCrypt;
import gate.sql.Link;
import gate.type.Hierarchy;
import gate.type.ID;
import gate.util.Toolkit;
import jakarta.enterprise.context.Dependent;
import java.time.LocalDateTime;

@Dependent
public class GateControl extends gate.base.Control
{

	public User select(ID id) throws InvalidUsernamePasswordException,
		HierarchyException
	{

		try (Link link = Link.of("Gate");
			GateDao dao = new GateDao(link))
		{
			User user = dao.select(id);

			if (user.isDisabled())
				throw new InvalidUsernamePasswordException();
			if (user.getRole().getId() == null)
				throw new InvalidUsernamePasswordException();

			var roles = dao.getRoles();
			Hierarchy.setup(roles);
			user.setRole(roles.stream().filter(user.getRole()::equals).findAny()
				.orElseThrow(() -> new HierarchyException("User role not found")));
			return user;
		}
	}

	public User select(String username) throws InvalidUsernamePasswordException,
		HierarchyException
	{
		if (Toolkit.isEmpty(username) || username.length() > 64)
			throw new InvalidUsernamePasswordException();

		try (Link link = Link.of("Gate");
			GateDao dao = new GateDao(link))
		{
			User user = dao.select(username);

			if (user.isDisabled())
				throw new InvalidUsernamePasswordException();
			if (user.getRole().getId() == null)
				throw new InvalidUsernamePasswordException();

			var roles = dao.getRoles();
			Hierarchy.setup(roles);
			user.setRole(roles.stream().filter(user.getRole()::equals).findAny()
				.orElseThrow(() -> new HierarchyException("User role not found")));
			return user;
		}
	}

	public void update(User user, LocalDateTime activity)
	{
		try (Link link = Link.of("Gate");
			GateDao dao = new GateDao(link))
		{
			dao.update(user, activity);
		}
	}
	
	public void update(User user, BCrypt password)
	{
		try (Link link = Link.of("Gate");
			GateDao dao = new GateDao(link))
		{
			dao.update(user, password);
		}
	}
}
