package gate.authenticator;

import gate.error.AuthenticatorException;
import gate.error.InvalidPasswordException;
import gate.error.InvalidUsernameException;
import gate.type.collections.StringList;
import java.util.Iterator;

public interface Authenticator
{

	void authenticate(String server,
		String username,
		String password) throws
		InvalidUsernameException,
		InvalidPasswordException,
		AuthenticatorException;

	default void authenticate(StringList servers, String username,
		String password) throws
		InvalidUsernameException,
		InvalidPasswordException,
		AuthenticatorException
	{
		Iterator<String> iter
			= servers.iterator();

		while (iter.hasNext())
		{
			String server = iter.next();
			try
			{
				authenticate(server, username, password);
				return;
			} catch (AuthenticatorException ex)
			{
				if (!iter.hasNext())
					throw ex;
			}

		}
	}
}
