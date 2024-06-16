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
import gate.util.SystemProperty;
import javax.servlet.http.HttpServletResponse;

public class DatabaseAuthenticator implements Authenticator
{

	private final String developer = SystemProperty.get("gate.developer").orElse(null);

	public DatabaseAuthenticator(Config configuration)
	{

	}

	@Override
	public String provider(ScreenServletRequest request, HttpServletResponse response)
	{
		return null;
	}

	@Override
	public User authenticate(GateControl control, ScreenServletRequest request,
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
			return developer != null ? control.select(developer) : null;

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
