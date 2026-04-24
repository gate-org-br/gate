package gate.authenticator;

import gate.catalog.UserCatalog;
import gate.entity.User;
import gate.error.AuthenticationException;
import gate.error.BadRequestException;
import gate.error.InvalidUsernamePasswordException;
import gate.http.BasicAuthorization;
import gate.http.ScreenServletRequest;
import gate.security.hash.BCrypt;
import gate.security.hash.MD5;
import jakarta.servlet.http.HttpServletResponse;

public class DatabaseAuthenticator implements Authenticator
{
	private final UserCatalog userCatalog;

	public DatabaseAuthenticator(UserCatalog userCatalog)
	{
		this.userCatalog = userCatalog;
	}

	@Override
	public String provider(ScreenServletRequest request, HttpServletResponse response)
	{
		return null;
	}

	@Override
	public boolean hasCredentials(ScreenServletRequest request) throws AuthenticationException
	{
		return request.getAuthorization() instanceof BasicAuthorization;
	}

	@Override
	public User authenticate(ScreenServletRequest request,
	                         HttpServletResponse response)
	{
		var authorization = (BasicAuthorization) request.getAuthorization();
		if (authorization == null)
			throw new BadRequestException("Missing user credentials");

		User user = userCatalog.select(authorization.username());

		if (user.getPassword().length() == 32)
		{
			if (!MD5.of(user.getPassword())
					.verify(authorization.password()))
				throw new InvalidUsernamePasswordException();
			userCatalog.update(user, gate.entity.User::getPassword, BCrypt.digest(authorization.password()));

		} else if (!BCrypt.of(user.getPassword())
				.verify(authorization.password()))
			throw new InvalidUsernamePasswordException();

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
