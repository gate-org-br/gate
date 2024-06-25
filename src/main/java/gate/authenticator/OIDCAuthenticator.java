package gate.authenticator;

import gate.GateControl;
import gate.cache.Cache;
import gate.entity.User;
import gate.error.AuthenticationException;
import gate.error.AuthenticatorException;
import gate.error.BadRequestException;
import gate.error.DefaultPasswordException;
import gate.error.HierarchyException;
import gate.error.InvalidPasswordException;
import gate.error.InvalidUsernameException;
import gate.http.Authorization;
import gate.http.BasicAuthorization;
import gate.http.BearerAuthorization;
import gate.http.ScreenServletRequest;
import gate.io.URL;
import gate.lang.json.JsonObject;
import gate.util.Parameters;
import gate.util.SecuritySessions;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import javax.servlet.http.HttpServletResponse;

public class OIDCAuthenticator implements Authenticator
{

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
	private final Cache<PublicKey> publicKey;

	public OIDCAuthenticator(Config config)
	{

		clientId = config.getProperty("oidc.client_id").orElseThrow(() -> new AuthenticatorException("Missing oidc.client_id configuration parameter"));
		clientSecret = config.getProperty("oidc.client_secret").orElseThrow(() -> new AuthenticatorException("Missing oidc.client_secret configuration parameter"));
		provider = config.getProperty("oidc.provider").orElseThrow(() -> new AuthenticatorException("Missing oidc.provider configuration parameter"));;
		configurationEndpoint = config.getProperty("oidc.configuration_endpoint").orElse(provider + "/.well-known/openid-configuration");
		redirectUri = config.getProperty("oidc.redirect_uri").orElse("${server}/Gate");
		userId = config.getProperty("oidc.user_id").orElse("email");
		scope = config.getProperty("oidc.scope").orElse("openid email profile");
		logoutUri = config.getProperty("oidc.logout_uri").orElse(null);
		configuration = Cache.of(this::fetchConfiguration);
		authorizationEndpoint = Cache.of(() -> config.getProperty("oidc.authorization_endpoint").orElseGet(() -> getEndpoint("authorization_endpoint")));
		tokenEndpoint = Cache.of(() -> config.getProperty("oidc.token_endpoint").orElseGet(() -> getEndpoint("token_endpoint")));
		userInfoEndpoint = Cache.of(() -> config.getProperty("oidc.userinfo_endpoint").orElseGet(() -> getEndpoint("userinfo_endpoint")));
		jwksUri = Cache.of(() -> config.getProperty("oidc.jwks_uri").orElseGet(() -> getEndpoint("jwks_uri")));
		publicKey = Cache.of(this::fetchPublicKey);
	}

	@Override
	public String provider(ScreenServletRequest request, HttpServletResponse response)
	{
		String session = SESSIONS.create();
		return new URL(authorizationEndpoint.get())
			.setParameter("response_type", "code")
			.setParameter("client_id", clientId)
			.setParameter("redirect_uri", redirectUri)
			.setParameter("scope", scope)
			.setParameter("state", session)
			.toString();
	}

	@Override
	public User authenticate(GateControl control, ScreenServletRequest request, HttpServletResponse response)
		throws InvalidPasswordException, InvalidUsernameException, HierarchyException, DefaultPasswordException, BadRequestException
	{
		try
		{
			JsonObject tokens = getTokens(request);

			if (tokens.containsKey("id_token"))
			{
				Jwt<Header, Claims> jwt = tokens.getString("id_token")
					.map(Jwts.parserBuilder()
						.setSigningKey(publicKey.get()).build()::parse)
					.orElseThrow(() -> new AuthenticatorException("Error trying to get id token from auth provider"));

				if (jwt.getBody().containsKey(userId))
					return control.select((String) jwt.getBody().get("email", String.class));
			}

			String accessToken = tokens
				.getString("access_token")
				.orElseThrow(() -> new AuthenticatorException("Error trying to get access token from auth provider"));

			JsonObject userInfo = new URL(userInfoEndpoint.get())
				.setAuthorization(new BearerAuthorization(accessToken))
				.get()
				.readJsonObject()
				.orElseThrow(() -> new AuthenticatorException("Error trying to get user info from auth provider"));

			return control.select(userInfo.getString(userId)
				.orElseThrow(() -> new AuthenticatorException("Error trying to get user user id from auth provider")));

		} catch (IOException | AuthenticationException ex)
		{
			throw new BadRequestException();
		}
	}

	@Override
	public String logoutUri(gate.http.ScreenServletRequest request)
	{
		if (logoutUri == null)
			return null;

		return new URL(logoutUri)
			.setParameter("client_id", clientId)
			.setParameter("post_logout_redirect_uri", request.getRequestURL().toString())
			.toString();
	}

	private JsonObject getTokens(ScreenServletRequest request) throws IOException, BadRequestException, AuthenticationException
	{
		var code = request.getParameter("code");
		if (code != null)
		{
			var state = request.getParameter("state");
			if (state == null || !SESSIONS.check(state))
				throw new BadRequestException("Bad request");

			return new URL(tokenEndpoint.get())
				.post(new Parameters()
					.set("grant_type", "authorization_code")
					.set("code", code)
					.set("scope", scope)
					.set("client_id", clientId)
					.set("client_secret", clientSecret)
					.set("redirect_uri", redirectUri))
				.readJsonObject()
				.orElseThrow(() -> new AuthenticatorException("Error trying to get token from auth provider"));
		}

		Authorization authorization = request.getAuthorization().orElseThrow(()
			-> new AuthenticatorException("Error trying to get token from auth provider"));

		if (authorization instanceof BasicAuthorization)
			return new URL(tokenEndpoint.get())
				.post(new Parameters()
					.set("grant_type", "password")
					.set("scope", scope)
					.set("client_id", clientId)
					.set("client_secret", clientSecret)
					.set("username", ((BasicAuthorization) authorization).username())
					.set("password", ((BasicAuthorization) authorization).password()))
				.readJsonObject()
				.orElseThrow(() -> new AuthenticatorException("Error trying to get token from auth provider"));

		if (authorization instanceof BearerAuthorization)
			return new JsonObject()
				.setString("access_token", ((BearerAuthorization) authorization).token());

		throw new BadRequestException();
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

	private PublicKey fetchPublicKey()
	{
		try
		{
			JsonObject object = new URL(jwksUri.get())
				.get()
				.readJsonObject()
				.flatMap(e -> e.getJsonArray("keys"))
				.flatMap(e -> e.stream().findFirst())
				.map(e -> (JsonObject) e)
				.orElseThrow(() -> new AuthenticatorException("Error trying to get public key from auth provider"));

			BigInteger modulus = object.getString("n")
				.map(Base64.getUrlDecoder()::decode)
				.map(e -> new BigInteger(1, e))
				.orElseThrow(() -> new AuthenticatorException("Error trying to get public key modulus from auth provider"));

			BigInteger exponent = object.getString("e")
				.map(Base64.getUrlDecoder()::decode)
				.map(e -> new BigInteger(1, e))
				.orElseThrow(() -> new AuthenticatorException("Error trying to get public key exponent from auth provider"));

			RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);
			return KeyFactory.getInstance("RSA").generatePublic(spec);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException ex)
		{
			throw new AuthenticatorException(ex);
		}
	}

	private String getEndpoint(String endpointKey)
	{
		return configuration.get().getString(endpointKey)
			.orElseThrow(() -> new AuthenticatorException("Error trying to get " + endpointKey + " from provider"));
	}
}
