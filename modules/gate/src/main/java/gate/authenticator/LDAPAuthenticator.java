package gate.authenticator;

import gate.catalog.UserCatalog;
import gate.entity.User;
import gate.error.AuthenticatorException;
import gate.error.InvalidUsernamePasswordException;
import gate.http.BasicAuthorization;
import gate.http.ScreenServletRequest;
import gate.security.hash.BCrypt;
import gate.security.hash.MD5;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.*;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import java.util.Hashtable;

public class LDAPAuthenticator implements Authenticator
{


	private final String server;
	private final String clientUsername;
	private final String clientPassword;
	private final String securityProtocol;
	private final String rootContext;
	private final boolean databaseFallback;
	private final UserCatalog userCatalog;

	public LDAPAuthenticator(AuthConfig config, UserCatalog userCatalog)
	{
		this.userCatalog = userCatalog;
		this.server = config.getProperty("ldap.server").orElseThrow(() -> new AuthenticatorException("Missing ldap.server"));
		this.securityProtocol = config.getProperty("ldap.security_protocol").orElse(null);
		this.clientUsername = config.getProperty("ldap.client_username").orElse(null);
		this.clientPassword = config.getProperty("ldap.client_password").orElse(null);
		this.databaseFallback = config.getProperty("ldap.database_fallback")
				.map("true"::equals)
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
		return request.getAuthorization() instanceof BasicAuthorization;
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
		parameters.put("com.sun.jndi.ldap.connect.timeout", "5000");
		parameters.put("com.sun.jndi.ldap.read.timeout", "5000");

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
	public User authenticate(ScreenServletRequest request,
	                         HttpServletResponse response)
	{
		var authorization = (BasicAuthorization) request.getAuthorization();

		User user = userCatalog.select(authorization.username());

		try
		{
			if (clientUsername != null && clientPassword != null)
			{
				String dn = getUniqueID(clientUsername, clientPassword, authorization);
				if (dn == null)
				{
					if (!databaseFallback)
						throw new InvalidUsernamePasswordException();

					if (user.getPassword().length() == 32)
					{
						if (!MD5.of(user.getPassword())
								.verify(authorization.password()))
							throw new InvalidUsernamePasswordException();
						userCatalog.update(user, gate.entity.User::getPassword, BCrypt.digest(authorization.password()));
					} else if (!BCrypt.of(user.getPassword())
							.verify(authorization.password()))
						throw new InvalidUsernamePasswordException();
				} else
					getDirContext(dn, authorization.password()).close();
			} else
				getDirContext(authorization.username(), authorization.password()).close();

			return user;

		} catch (CommunicationException ex)
		{
			throw new AuthenticatorException(ex);
		} catch (NamingException ex)
		{
			if (ex instanceof AuthenticationException)
				throw new InvalidUsernamePasswordException();
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