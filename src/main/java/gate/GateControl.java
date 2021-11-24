package gate;

import gate.annotation.DataSource;
import gate.authenticator.LDAPAuthenticator;
import gate.entity.Bond;
import gate.entity.Org;
import gate.entity.Role;
import gate.entity.User;
import gate.error.AppException;
import gate.error.AuthenticatorException;
import gate.error.DefaultPasswordException;
import gate.error.DuplicateException;
import gate.error.InvalidCircularRelationException;
import gate.error.InvalidPasswordException;
import gate.error.InvalidServiceException;
import gate.error.InvalidUsernameException;
import gate.error.NotFoundException;
import gate.sql.Link;
import gate.sql.LinkSource;
import gate.type.Hierarchy;
import gate.type.MD5;
import gate.util.Toolkit;
import java.util.List;
import java.util.stream.Collectors;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class GateControl extends gate.base.Control
{

	@Inject
	@DataSource("Gate")
	LinkSource linksource;

	private static final LDAPAuthenticator AUTHENTICATOR = new LDAPAuthenticator();

	public User select(String username) throws InvalidUsernameException, DuplicateException,
		InvalidCircularRelationException, NotFoundException
	{
		if (Toolkit.isEmpty(username) || username.length() > 64)
			throw new InvalidUsernameException();

		try ( Link link = linksource.getLink();
			 GateDao dao = new GateDao(link))
		{
			User user = dao.select(username)
				.orElseThrow(InvalidUsernameException::new);

			if (user.isDisabled())
				throw new InvalidUsernameException();
			if (user.getRole().getId() == null)
				throw new InvalidUsernameException();

			List<Role> roles = dao.getRoles();
			Hierarchy.setup(roles);

			List<gate.entity.Auth> auths = dao.getAuths();
			List<Bond> funcs = dao.getBonds();

			funcs.forEach(func -> func.getFunc().setAuths(auths.stream().filter(auth -> func.getFunc().equals(auth.getFunc())).collect(Collectors.toList())));

			user.setAuths(auths.stream().filter(auth -> user.equals(auth.getUser())).collect(Collectors.toList()));
			user.setFuncs(funcs.stream().filter(func -> user.equals(func.getUser())).map(Bond::getFunc).collect(Collectors.toList()));

			roles.forEach(role -> role.setAuths(auths.stream().filter(auth -> role.equals(auth.getRole())).collect(Collectors.toList())));
			roles.forEach(role -> role.setFuncs(funcs.stream().filter(func -> role.equals(func.getRole())).map(Bond::getFunc).collect(Collectors.toList())));

			user.setRole(roles.stream().filter(e -> user.getRole().equals(e)).findAny().orElseThrow(NotFoundException::new));

			if (user.getRole().isDisabled())
				throw new InvalidUsernameException();
			return user;
		}
	}

	public User select(Org org,
		String username,
		String password) throws
		InvalidServiceException,
		InvalidUsernameException,
		InvalidPasswordException,
		DefaultPasswordException,
		AuthenticatorException,
		DuplicateException,
		InvalidCircularRelationException,
		NotFoundException

	{

		try ( Link link = linksource.getLink();
			 GateDao dao = new GateDao(link))
		{
			User user = dao.select(username)
				.orElseThrow(InvalidUsernameException::new);

			if (user.isDisabled())
				throw new InvalidUsernameException();
			if (user.getRole().getId() == null)
				throw new InvalidUsernameException();

			List<Role> roles = dao.getRoles();
			Hierarchy.setup(roles);

			List<gate.entity.Auth> auths = dao.getAuths();
			List<Bond> funcs = dao.getBonds();

			funcs.forEach(func -> func.getFunc().setAuths(auths.stream().filter(auth -> func.getFunc().equals(auth.getFunc())).collect(Collectors.toList())));

			user.setAuths(auths.stream().filter(auth -> user.equals(auth.getUser())).collect(Collectors.toList()));
			user.setFuncs(funcs.stream().filter(func -> user.equals(func.getUser())).map(Bond::getFunc).collect(Collectors.toList()));

			roles.forEach(role -> role.setAuths(auths.stream().filter(auth -> role.equals(auth.getRole())).collect(Collectors.toList())));
			roles.forEach(role -> role.setFuncs(funcs.stream().filter(func -> role.equals(func.getRole())).map(Bond::getFunc).collect(Collectors.toList())));

			user.setRole(roles.stream().filter(e -> user.getRole().equals(e)).findAny().orElseThrow(NotFoundException::new));

			if (user.getRole().isDisabled())
				throw new InvalidUsernameException();

			if (!org.getAuthenticators().isEmpty())
			{
				try
				{
					AUTHENTICATOR.authenticate(org.getAuthenticators(),
						username, password);
				} catch (InvalidUsernameException ex)
				{
					if (!user.getPassword().equals(MD5.digest(password).toString()))
						throw new InvalidPasswordException();
				}
			} else if (!user.getPassword().equals(MD5.digest(password).toString()))
				throw new InvalidPasswordException();

			if (username.equals(password))
				throw new DefaultPasswordException();

			return user;
		}
	}

	public void update(User user) throws AppException
	{
		try ( Link link = linksource.getLink();
			 GateDao dao = new GateDao(link))
		{
			dao.update(user);
		}
	}

	public void update(User user, String password) throws AppException
	{
		try ( Link link = linksource.getLink();
			 GateDao dao = new GateDao(link))
		{
			dao.update(user, password);
		}
	}

}
