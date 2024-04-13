package gate.authenticator;

import gate.GateControl;
import gate.entity.User;
import gate.error.AuthenticatorException;
import gate.error.DefaultPasswordException;
import gate.error.HierarchyException;
import gate.error.HttpException;
import gate.error.InvalidPasswordException;
import gate.http.ScreenServletRequest;
import gate.type.MD5;
import gate.util.SystemProperty;
import jakarta.servlet.http.HttpServletResponse;
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

	private final String server;
	private final GateControl control;

	private LDAPAuthenticator(GateControl control, String server)
	{
		this.server = server;
		this.control = control;
	}

	public static LDAPAuthenticator of(GateControl control)
	{
		return new LDAPAuthenticator(control, SystemProperty.get("gate.auth.ldap.server")
			.orElseThrow(() -> new AuthenticatorException("Missing gate.auth.ldap.server system property")));
	}

	@Override
	public String provider(ScreenServletRequest request, HttpServletResponse response)
	{
		return null;
	}

	@Override
	public boolean hasCredentials(ScreenServletRequest request) throws gate.error.AuthenticationException
	{
		return request.getBasicAuthorization().isPresent();
	}

	@Override
	public User authenticate(ScreenServletRequest request, HttpServletResponse response)
		throws HttpException, gate.error.AuthenticationException, HierarchyException
	{

		var authorization = request.getBasicAuthorization().orElse(null);

		if (authorization == null)
			return null;

		User user = control.select(authorization.username());

		try
		{

			@SuppressWarnings("UseOfObsoleteCollectionType")
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
						authorization.username(), authorization.username()
				}, controls);

				if (enumeration.hasMore())
				{
					try
					{
						String dn = enumeration.next().getNameInNamespace();
						parameters.put(Context.SECURITY_PRINCIPAL, dn);
						parameters.put(Context.SECURITY_CREDENTIALS, authorization.password());
						parameters.put(Context.SECURITY_AUTHENTICATION, "simple");
						DirContext authContext = new InitialDirContext(parameters);
						authContext.close();
					} catch (AuthenticationException ex)
					{
						throw new InvalidPasswordException();
					}
				} else
				{
					if (authorization.username().equals(authorization.password()))
						throw new DefaultPasswordException();

					if (!MD5.digest(authorization.password())
						.toString().equals(user.getPassword()))
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

	@Override
	public String logoutUri(gate.http.ScreenServletRequest request)
	{
		return null;
	}

	@Override
	public Type getType()
	{
		return Authenticator.Type.LDAP;
	}
}
