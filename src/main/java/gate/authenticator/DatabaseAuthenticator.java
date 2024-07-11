package gate.authenticator;

import gate.GateControl;
import gate.entity.User;
import gate.error.AuthenticationException;
import gate.error.DefaultPasswordException;
import gate.error.HierarchyException;
import gate.error.HttpException;
import gate.error.InvalidPasswordException;
import gate.http.ScreenServletRequest;
import gate.type.MD5;
import jakarta.servlet.http.HttpServletResponse;

public class DatabaseAuthenticator implements Authenticator
{

	private final GateControl control;

	public DatabaseAuthenticator(GateControl control, AuthConfig config)
	{
		this.control = control;
	}

	@Override
	public String provider(ScreenServletRequest request, HttpServletResponse response)
	{
		return null;
	}

	@Override
	public boolean hasCredentials(ScreenServletRequest request) throws AuthenticationException
	{
		return request.getBasicAuthorization().isPresent();
	}

	@Override
	public User authenticate(ScreenServletRequest request,
		HttpServletResponse response)
		throws HttpException, AuthenticationException, HierarchyException
	{

		var authorization = request.getBasicAuthorization().orElse(null);

		User user = control.select(authorization.username());

		if (MD5.digest(user.getUsername()).toString()
			.equals(user.getPassword()))
			throw new DefaultPasswordException();

		if (!MD5.digest(authorization.password()).toString().equals(user.getPassword()))
			throw new InvalidPasswordException();

		return user;
	}

	@Override
	public String logoutUri(gate.http.ScreenServletRequest request)
	{
		return null;
	}

	@Override
	public Type getType()
	{
		return Authenticator.Type.DATABASE;
	}
}
