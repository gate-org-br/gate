package gate;

import gate.annotation.DataSource;
import gate.entity.Role;
import gate.entity.User;
import gate.error.HierarchyException;
import gate.error.InvalidUsernameException;
import gate.sql.Link;
import gate.sql.LinkSource;
import gate.type.Hierarchy;
import gate.util.Toolkit;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import java.util.List;

@Dependent
public class GateControl extends gate.base.Control
{

	@Inject
	@DataSource("Gate")
	LinkSource linksource;

	public User select(String username) throws InvalidUsernameException, HierarchyException
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

			List<Role> roles = dao.getRoles();
			Hierarchy.setup(roles);

			List<gate.entity.Auth> auths = dao.getAuths();

			user.getFuncs().stream().forEach(func -> func.setAuths(auths.stream().filter(auth -> auth.getFunc().equals(func)).toList()));
			roles.stream().flatMap(role -> role.getFuncs().stream()).forEach(func -> func.setAuths(auths.stream().filter(auth -> auth.getFunc().equals(func)).toList()));

			user.setAuths(auths.stream().filter(auth -> user.equals(auth.getUser())).toList());

			roles.forEach(role -> role.setAuths(auths.stream().filter(auth -> role.equals(auth.getRole())).toList()));

			user.setRole(roles.stream().filter(e -> user.getRole().equals(e)).findAny().orElseThrow(() -> new HierarchyException("Perfil do usuário não encontrado")));

			if (user.getRole().isDisabled())
				throw new InvalidUsernameException();
			return user;
		}
	}
}
