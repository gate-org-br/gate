package gate.authenticator;

import gate.GateControl;
import gate.cache.Cache;
import gate.entity.User;
import gate.error.AuthenticationException;
import gate.error.AuthenticatorException;
import gate.error.HierarchyException;
import gate.http.BasicAuthorization;
import gate.http.BearerAuthorization;
import gate.http.ScreenServletRequest;
import gate.io.URL;
import gate.lang.json.JsonObject;
import gate.security.JWKSPublicKeyParser;
import gate.util.Parameters;
import gate.security.SecuritySessions;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import java.io.IOException;
import java.security.PublicKey;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;

public class OIDCAuthenticator implements Authenticator
{

	private final GateControl control;

	private static final SecuritySessions SESSIONS = SecuritySessions.of(60000);

	private final String provider;
	private final String clientId;
	private final String clientSecret;
	private final String redirectUri;
	private final String scope;
	private final String configurationEndpoint;
	private final String userId;
	private final String logoutUri;
	private final Cache<JsonObject> configuration;
	private final Cache<String> authorizationEndpoint;
	private final Cache<String> tokenEndpoint;
	private final Cache<String> userInfoEndpoint;
	private final Cache<String> jwksUri;
	private final Cache<Map<String, PublicKey>> publicKeys;

	private String getCallback(ScreenServletRequest request)
	{
		return redirectUri.replace("${server}", "%s://%s:%d"
				.formatted(request.getScheme(),
						request.getLocalAddr(),
						request.getLocalPort()));
	}

	public OIDCAuthenticator(GateControl control, AuthConfig config)
	{
		this.control = control;
		clientId = config.getProperty("oidc.client_id").orElseThrow(() -> new AuthenticatorException("Missing oidc.client_id configuration parameter"));
		clientSecret = config.getProperty("oidc.client_secret").orElseThrow(() -> new AuthenticatorException("Missing oidc.client_secret configuration parameter"));
		provider = config.getProperty("oidc.provider").orElseThrow(() -> new AuthenticatorException("Missing oidc.provider configuration parameter"));
		configurationEndpoint = config.getProperty("oidc.configuration_endpoint").orElse(provider + "/.well-known/openid-configuration");
		redirectUri = config.getProperty("oidc.redirect_uri").orElse("${server}/Gate?authenticator=" + config.name());
		userId = config.getProperty("oidc.user_id").orElse("email");
		scope = config.getProperty("oidc.scope").orElse("openid email profile");
		logoutUri = config.getProperty("oidc.logout_uri").orElse(null);
		configuration = Cache.of(this::fetchConfiguration);
		authorizationEndpoint = Cache.of(() -> config.getProperty("oidc.authorization_endpoint").orElseGet(() -> getEndpoint("authorization_endpoint")));
		tokenEndpoint = Cache.of(() -> config.getProperty("oidc.token_endpoint").orElseGet(() -> getEndpoint("token_endpoint")));
		userInfoEndpoint = Cache.of(() -> config.getProperty("oidc.userinfo_endpoint").orElseGet(() -> getEndpoint("userinfo_endpoint")));
		jwksUri = Cache.of(() -> config.getProperty("oidc.jwks_uri").orElseGet(() -> getEndpoint("jwks_uri")));
		publicKeys = Cache.of(this::fetchPublicKeys);
	}

	@Override
	public String provider(ScreenServletRequest request, HttpServletResponse response)
	{
		return new URL(authorizationEndpoint.get())
				.setParameter("response_type", "code")
				.setParameter("client_id", clientId)
				.setParameter("redirect_uri", getCallback(request))
				.setParameter("scope", scope)
				.setParameter("state", SESSIONS.create())
				.setParameter("nonce", SESSIONS.create())
				.toString();
	}

	@Override
	public boolean hasCredentials(ScreenServletRequest request) throws AuthenticationException
	{
		return request.getParameter("code") != null
				|| request.getParameter("accessCode") != null
				|| request.getBasicAuthorization().isPresent();
	}

	@Override
	public User authenticate(ScreenServletRequest request, HttpServletResponse response)
			throws AuthenticationException, HierarchyException
	{
		try
		{
			if (request.getParameter("code") != null)
				return authorizationCodeFlow(control, request);
			else if (request.getParameter("accessCode") != null)
				return partialAuthorizationCodeFlow(control,
						request.getParameter("accessCode"));
			else if (request.getAuthorization().orElse(null) instanceof BasicAuthorization authorization)
				return resourceOwnerPasswordCredentialsFlow(control, authorization);
			else
				throw new AuthenticationException("Attempt to authenticate without supplying credentials");
		} catch (IOException | RuntimeException ex)
		{
			throw new AuthenticationException();
		}
	}

	private User authorizationCodeFlow(GateControl control, ScreenServletRequest request)
			throws AuthenticationException, HierarchyException, IOException
	{
		var code = request.getParameter("code");

		var state = request.getParameter("state");
		if (state == null || !SESSIONS.check(state))
			throw new AuthenticationException("Error validating state");

		var tokens = new URL(tokenEndpoint.get())
				.post(new Parameters()
						.set("grant_type", "authorization_code")
						.set("code", code)
						.set("scope", scope)
						.set("client_id", clientId)
						.set("client_secret", clientSecret)
						.set("redirect_uri", getCallback(request)))
				.readJsonObject()
				.orElseThrow(() -> new AuthenticationException("Error trying to get token from auth provider"));

		if (tokens.containsKey("id_token"))
		{

			var idToken = tokens.getString("id_token").orElseThrow(() -> new AuthenticationException("Error trying to get id token from auth provider"));
			var jwt = Jwts.parser().keyLocator(this::getPublicKey).build().parse(idToken);

			if (jwt.getPayload() instanceof Claims claims)
			{

				if (!SESSIONS.check(claims.get("nonce", String.class)))
					throw new AuthenticationException("Error validating the id token from auth provider");

				if (claims.containsKey(userId))
					return control.select(claims.get(userId, String.class));
			}
		}

		String accessToken = tokens
				.getString("access_token")
				.orElseThrow(() -> new AuthenticationException("Error trying to get access token from auth provider"));

		JsonObject userInfo = new URL(userInfoEndpoint.get())
				.setAuthorization(new BearerAuthorization(accessToken))
				.get()
				.readJsonObject()
				.orElseThrow(() -> new AuthenticationException("Error trying to get user info from auth provider"));

		return control.select(userInfo.getString(userId)
				.orElseThrow(() -> new AuthenticationException("Error trying to get user user id from auth provider")));
	}

	private User partialAuthorizationCodeFlow(GateControl control, String accessCode)
			throws AuthenticationException, HierarchyException, IOException
	{
		var token = accessCode;
		if (token.split("\\.").length == 3)
		{
			var jwt = Jwts.parser().keyLocator(this::getPublicKey).build().parse(token);
			if (!(jwt.getPayload() instanceof Claims claims) || !claims.containsKey(userId))
				throw new AuthenticationException("Invalid token");
			return control.select(claims.get(userId, String.class));
		}

		JsonObject userInfo = new URL(userInfoEndpoint.get())
				.setAuthorization(new BearerAuthorization(token))
				.get()
				.readJsonObject()
				.orElseThrow(() -> new AuthenticationException("Error trying to get user info from auth provider"));

		return control.select(userInfo.getString(userId)
				.orElseThrow(() -> new AuthenticationException("Error trying to get user id from auth provider")));
	}

	private User resourceOwnerPasswordCredentialsFlow(GateControl control, BasicAuthorization authorization)
			throws AuthenticationException, HierarchyException, IOException
	{

		JsonObject tokens = new URL(tokenEndpoint.get())
				.post(new Parameters()
						.set("grant_type", "password")
						.set("username", authorization.username())
						.set("password", authorization.password())
						.set("client_id", clientId)
						.set("client_secret", clientSecret)
						.set("scope", scope))
				.readJsonObject()
				.orElseThrow(() -> new AuthenticationException("Error obtaining access token"));

		if (tokens.containsKey("id_token"))
		{
			var idToken = tokens.getString("id_token").orElseThrow(()
					-> new AuthenticationException("Error trying to get id token from auth provider"));
			var jwt = Jwts.parser().keyLocator(this::getPublicKey).build().parse(idToken);

			if (jwt.getPayload() instanceof Claims claims)
				if (claims.containsKey(userId))
					return control.select(claims.get(userId, String.class));
		}

		String accessToken = tokens.getString("access_token")
				.orElseThrow(() -> new AuthenticationException("No access token in response"));

		JsonObject userInfo = new URL(userInfoEndpoint.get())
				.setAuthorization(new BearerAuthorization(accessToken))
				.get()
				.readJsonObject()
				.orElseThrow(() -> new AuthenticationException("Error retrieving user info"));

		return control.select(userInfo.getString(userId)
				.orElseThrow(() -> new AuthenticationException("Error trying to get user user id from auth provider")));
	}

	@Override
	public String logoutUri(gate.http.ScreenServletRequest request)
	{
		String url = logoutUri;
		if (url == null)
			return null;

		return new URL(url)
				.setParameter("client_id", clientId)
				.setParameter("post_logout_redirect_uri", request.getRequestURL().toString())
				.toString();
	}

	private JsonObject fetchConfiguration()
	{
		try
		{
			return new URL(configurationEndpoint)
					.get()
					.readJsonObject()
					.orElseThrow(() -> new AuthenticatorException("Error trying to get configuration from auth provider"));
		} catch (IOException ex)
		{
			throw new AuthenticatorException(ex);
		}
	}

	private Map<String, PublicKey> fetchPublicKeys()
	{
		try
		{
			return new URL(jwksUri.get())
					.get()
					.readJsonObject()
					.flatMap(e -> e.getJsonArray("keys"))
					.orElseThrow()
					.stream()
					.map(e -> (JsonObject) e)
					.collect(Collectors.toMap(e -> e.getString("kid")
					.orElseThrow(() -> new AuthenticatorException("Error trying to get public key from auth provider")),
							JWKSPublicKeyParser::parse));
		} catch (IOException ex)
		{
			throw new AuthenticatorException(ex);
		}
	}

	private String getEndpoint(String endpointKey)
	{
		return configuration.get().getString(endpointKey)
				.orElseThrow(() -> new AuthenticatorException("Error trying to get " + endpointKey + " from provider"));
	}

	private PublicKey getPublicKey(Header header)
	{
		if (!header.containsKey("kid"))
			return publicKeys.get().values().stream().findFirst()
					.orElseThrow(() -> new AuthenticatorException("Error trying to get public key from auth provider"));

		var publicKey = publicKeys.get().get((String) header.get("kid"));

		if (publicKey == null)
			return publicKeys.get().values().stream().findFirst()
					.orElseThrow(() -> new AuthenticatorException("Error trying to get public key from auth provider"));

		return publicKey;
	}

	@Override
	public Type getType()
	{
		return Authenticator.Type.OIDC;
	}
}
