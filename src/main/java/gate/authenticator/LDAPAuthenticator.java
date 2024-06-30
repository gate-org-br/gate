package gate.authenticator;

import gate.GateControl;
import gate.entity.User;
import gate.error.AuthenticatorException;
import gate.error.DefaultPasswordException;
import gate.error.HierarchyException;
import gate.error.InvalidPasswordException;
import gate.error.InvalidUsernameException;
import gate.http.BasicAuthorization;
import gate.http.ScreenServletRequest;
import gate.type.MD5;
import gate.util.SystemProperty;
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
import javax.servlet.http.HttpServletResponse;

public class LDAPAuthenticator implements Authenticator
{

	private final GateControl control;

	private final String server;
	private final String clientUsername;
	private final String clientPassword;
	private final String securityProtocol;
	private final String rootContext;
	private final boolean databaseFallback;
	private final String developer = SystemProperty.get("gate.developer").orElse(null);

	public LDAPAuthenticator(GateControl control,
		AuthConfig config)
	{
		this.control = control;
		this.server = config.getProperty("ldap.server").orElseThrow(() -> new AuthenticatorException("Missing ldap.server"));
		this.securityProtocol = config.getProperty("ldap.security_protocol").orElse(null);
		this.clientUsername = config.getProperty("ldap.client_username").orElse(null);
		this.clientPassword = config.getProperty("ldap.client_password").orElse(null);
		this.databaseFallback = config.getProperty("ldap.database_fallback")
			.map(e -> "true".equals(e))
			.orElse(false);
		this.rootContext = config.getProperty("ldap.root_context").orElse("");
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

	private DirContext getDirContext(String username, String password) throws NamingException
	{
		@SuppressWarnings("UseOfObsoleteCollectionType")
		Hashtable<String, String> parameters = new Hashtable<>();
		parameters.put(Context.SECURITY_AUTHENTICATION, "simple");
		parameters.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");

		parameters.put(Context.PROVIDER_URL, server);
		parameters.put(Context.SECURITY_PRINCIPAL, username);
		parameters.put(Context.SECURITY_CREDENTIALS, password);

		if (securityProtocol != null)
			parameters.put(Context.SECURITY_PROTOCOL, securityProtocol);

		return new InitialDirContext(parameters);
	}

	private String getUniqueID(String username, String password,
		BasicAuthorization authorization) throws NamingException
	{
		DirContext serverContext = getDirContext(username, password);
		try
		{

			SearchControls controls = new SearchControls();
			controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			NamingEnumeration<SearchResult> enumeration = serverContext.search(rootContext, "(|(dn={0})(cn={1})(mail={2}))",
				new Object[]
				{
					authorization.username(), authorization.username(), authorization.username()
				}, controls);

			return enumeration.hasMore() ? enumeration.next().getNameInNamespace() : null;
		} finally
		{
			serverContext.close();
		}
	}

	@Override
	public User authenticate(ScreenServletRequest request, HttpServletResponse response)
		throws gate.error.AuthenticationException, HierarchyException
	{
		var authorization = request.getBasicAuthorization().orElse(null);

		if (authorization == null)
			return developer != null ? control.select(developer) : null;

		User user = control.select(authorization.username());

		try
		{
			if (clientUsername != null && clientPassword != null)
			{
				String dn = getUniqueID(clientUsername, clientPassword, authorization);
				if (dn == null)
				{
					if (!databaseFallback)
						throw new InvalidUsernameException();

					if (MD5.digest(authorization.username()).toString()
						.equals(user.getPassword()))
						throw new DefaultPasswordException();

					if (!MD5.digest(authorization.password())
						.toString().equals(user.getPassword()))
						throw new InvalidPasswordException();
				} else
					getDirContext(dn, authorization.password())
						.close();
			} else
				getDirContext(authorization.username(), authorization.password())
					.close();

			return user;

		} catch (CommunicationException ex)
		{
			throw new AuthenticatorException(ex);
		} catch (NamingException ex)
		{
			if (ex instanceof AuthenticationException)
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
