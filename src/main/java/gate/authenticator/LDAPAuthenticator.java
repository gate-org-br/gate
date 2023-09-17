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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LDAPAuthenticator implements Authenticator
{

	private final String server;
	private final GateControl control;

	private LDAPAuthenticator(GateControl control, String server)
	{
		this.server = server;
		this.control = control;
	}

	public static LDAPAuthenticator of(GateControl control, String server)
	{
		return new LDAPAuthenticator(control, server);
	}

	public static LDAPAuthenticator of(GateControl control)
	{
		String server = System.getProperty("gate.auth.ldap.server",
			System.getenv("gate.auth.ldap.server"));
		if (server == null)
			throw new java.lang.IllegalArgumentException("Missing gate.auth.ldap.server system property");
		return new LDAPAuthenticator(control, server);
	}

	@Override
	public String provider(HttpServletRequest request, HttpServletResponse response)
	{
		return null;
	}

	@Override
	public User authenticate(HttpServletRequest request, HttpServletResponse response)
		throws AuthenticatorException, InvalidPasswordException, InvalidUsernameException, HierarchyException, DefaultPasswordException
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

		if (username == null || username.isBlank())
			throw new InvalidUsernameException();

		if (password == null || password.isBlank())
			throw new InvalidPasswordException();

		User user = control.select(username);

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

				if (enumeration.hasMore())
				{
					try
					{
						String dn = enumeration.next().getNameInNamespace();
						parameters.put(Context.SECURITY_PRINCIPAL, dn);
						parameters.put(Context.SECURITY_CREDENTIALS, password);
						parameters.put(Context.SECURITY_AUTHENTICATION, "simple");
						DirContext authContext = new InitialDirContext(parameters);
						authContext.close();
					} catch (AuthenticationException ex)
					{
						throw new InvalidPasswordException();
					}
				} else
				{
					if (username.equals(password))
						throw new DefaultPasswordException();

					password = MD5.digest(password).toString();
					if (!password.equals(user.getPassword()))
						throw new InvalidPasswordException();
				}

			} finally
			{
				serverContext.close();
			}

			return user;

		} catch (CommunicationException ex)
		{
			throw new AuthenticatorException(ex);
		} catch (NamingException ex)
		{
			if (ex.getCause() instanceof AuthenticationException)
				throw new InvalidPasswordException();
			else
				throw new AuthenticatorException(ex);
		}
	}
}
