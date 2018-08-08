package gate.authenticator;

import gate.error.AuthenticatorException;
import gate.error.InvalidPasswordException;
import gate.error.InvalidServiceException;
import gate.error.InvalidUsernameException;
import java.util.Hashtable;
import javax.naming.AuthenticationException;
import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

public class LDAPAuthenticator implements Authenticator
{

	@Override
	public void authenticate(String server, String username, String password) throws AuthenticatorException, InvalidPasswordException, InvalidUsernameException, InvalidServiceException
	{
		try
		{
			try
			{
				Hashtable<String, String> parameters = new Hashtable<>();
				parameters.put(Context.PROVIDER_URL, server);
				parameters.put(Context.SECURITY_AUTHENTICATION, "none");
				parameters.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");

				DirContext serverContext = new InitialDirContext(parameters);
				try
				{
					SearchControls controls = new SearchControls();
					controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
					NamingEnumeration<SearchResult> enumeration
							= serverContext.search("", "(|(cn={0})(mail={1}))", new Object[]
							{
								username, username
					}, controls);

					if (!enumeration.hasMore())
						throw new InvalidUsernameException();

					String dn = enumeration.next().getNameInNamespace();
					parameters.put(Context.SECURITY_PRINCIPAL, dn);
					parameters.put(Context.SECURITY_CREDENTIALS, password);
					parameters.put(Context.SECURITY_AUTHENTICATION, "simple");
					DirContext authContext = new InitialDirContext(parameters);
					authContext.close();
				} finally
				{
					serverContext.close();
				}

			} catch (CommunicationException ex)
			{
				throw new InvalidServiceException();
			} catch (AuthenticationException e)
			{
				throw new InvalidPasswordException();
			}
		} catch (NamingException ex)
		{
			if (ex.getCause() instanceof AuthenticationException)
				throw new InvalidPasswordException();
			else
				throw new AuthenticatorException(ex);
		}
	}
}
