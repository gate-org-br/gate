package gate.authenticator;

import gate.GateControl;
import gate.entity.User;
import gate.error.AuthenticationException;
import gate.error.AuthenticatorException;
import gate.error.DefaultPasswordException;
import gate.error.HierarchyException;
import gate.error.InvalidPasswordException;
import gate.error.InvalidUsernameException;
import gate.http.ScreenServletRequest;
import gate.type.MD5;
import javax.servlet.http.HttpServletResponse;

public class DatabaseAuthenticator implements Authenticator
{

	private final GateControl control;

	private DatabaseAuthenticator(GateControl control)
	{
		this.control = control;
	}

	public static DatabaseAuthenticator of(GateControl control)
	{
		return new DatabaseAuthenticator(control);
	}

	@Override
	public String provider(ScreenServletRequest request, HttpServletResponse response)
	{
		return null;
	}

	@Override
	public User authenticate(ScreenServletRequest request,
		HttpServletResponse response)
		throws AuthenticatorException,
		InvalidPasswordException,
		InvalidUsernameException,
		HierarchyException,
		DefaultPasswordException,
		AuthenticationException
	{

		var authorization = request.getBasicAuthorization().orElse(null);

		if (authorization == null)
			return null;

		if (authorization.username().equals(authorization.password()))
			throw new DefaultPasswordException();

		User user = control.select(authorization.username());

		if (!MD5.digest(authorization.password()).toString().equals(user.getPassword()))
			throw new InvalidPasswordException();

		return user;
	}

	@Override
	public String logoutUri(gate.http.ScreenServletRequest request)
	{
		return null;
	}

}
