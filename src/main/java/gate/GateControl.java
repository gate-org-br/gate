package gate;

import gate.error.InvalidPasswordException;
import gate.error.InvalidUsernameException;
import gate.error.InvalidServiceException;
import gate.error.AuthenticatorException;
import gate.authenticator.LDAPAuthenticator;
import gate.entity.Org;
import gate.entity.User;
import gate.error.ConstraintViolationException;
import gate.error.DefaultPasswordException;
import gate.type.MD5;
import javax.enterprise.context.Dependent;

@Dependent
class GateControl extends gate.base.Control
{

	private static final LDAPAuthenticator AUTHENTICATOR = new LDAPAuthenticator();

	public User select(Org org,
			   String username,
			   String password) throws
		InvalidServiceException,
		InvalidUsernameException,
		InvalidPasswordException,
		DefaultPasswordException,
		AuthenticatorException

	{

		try (GateDao dao = new GateDao())
		{
			User user = dao.getUser(username)
				.orElseThrow(()
					-> new InvalidUsernameException());

			if (user.isDisabled())
				throw new InvalidUsernameException();
			if (user.getRole().getId() == null)
				throw new InvalidUsernameException();

			user.setRole(dao.getRole(user.getRole().getId()));
			if (user.getRole().isDisabled())
				throw new InvalidUsernameException();

			if (!org.getAuthenticators().isEmpty())
				AUTHENTICATOR.authenticate(org.getAuthenticators(), username, password);
			else if (!user.getPasswd().equals(MD5.digest(password).toString()))
				throw new InvalidPasswordException();

			if (username.equals(password))
				throw new DefaultPasswordException();

			return user;
		}
	}

	public boolean update(User user) throws ConstraintViolationException
	{
		try (GateDao dao = new GateDao())
		{
			return dao.update(user);
		}
	}
}
