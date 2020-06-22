package gate.authenticator;

import gate.error.InvalidPasswordException;
import gate.error.InvalidUsernameException;
import gate.error.InvalidServiceException;
import gate.error.AuthenticatorException;
import gate.type.collections.StringList;
import java.util.Iterator;

public interface Authenticator
{

	void authenticate(String server,
			  String username,
			  String password) throws
		InvalidUsernameException,
		InvalidPasswordException,
		InvalidServiceException,
		AuthenticatorException;

	default void authenticate(StringList servers, String username,
				  String password) throws
		InvalidUsernameException,
		InvalidPasswordException,
		InvalidServiceException,
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
			} catch (InvalidServiceException ex)
			{
				if (!iter.hasNext())
					throw ex;
			}

		}
	}
}
