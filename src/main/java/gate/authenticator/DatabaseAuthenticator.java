package gate.authenticator;

import gate.GateControl;
import gate.entity.User;
import gate.error.AuthenticatorException;
import gate.error.DefaultPasswordException;
import gate.error.HierarchyException;
import gate.error.InvalidPasswordException;
import gate.error.InvalidUsernameException;
import gate.type.MD5;
import java.nio.charset.Charset;
import java.util.Base64;
import javax.servlet.http.HttpServletRequest;
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
	public String provider(HttpServletRequest request, HttpServletResponse response)
	{
		return null;
	}

	@Override
	public User authenticate(HttpServletRequest request,
		HttpServletResponse response)
		throws AuthenticatorException,
		InvalidPasswordException,
		InvalidUsernameException,
		HierarchyException,
		DefaultPasswordException
	{
		String username = request.getParameter("$username");
		String password = request.getParameter("$password");

		if (username == null && password == null)
		{
			String authHeader = request.getHeader("Authorization");
			if (authHeader != null && authHeader.startsWith("Basic"))
			{
				String encoded = authHeader.substring("Basic".length()).trim();
				String decoded = new String(Base64.getDecoder().decode(encoded),
					Charset.forName("UTF-8"));
				int index = decoded.indexOf(":");
				username = decoded.substring(0, index);
				password = decoded.substring(index + 1);
			}
		}

		if (username == null && password == null)
			return null;

		if (username == null || username.isBlank())
			throw new InvalidUsernameException();

		if (password == null || password.isBlank())
			throw new InvalidPasswordException();

		if (username.equals(password))
			throw new DefaultPasswordException();

		password = MD5.digest(password).toString();

		User user = control.select(username);

		if (!password.equals(user.getPassword()))
			throw new InvalidPasswordException();
		return user;
	}
}
