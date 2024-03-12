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
import gate.util.SystemProperty;
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

	private final GateControl control;
	private static final SecuritySessions SESSIONS = SecuritySessions.of(60000);

	private final Cache<String> provider = Cache.of(() -> SystemProperty.get("gate.auth.oidc.provider")
		.orElseThrow(() -> new AuthenticatorException("Missing gate.auth.oidc.provider system property")));

	private final Cache<String> clientId = Cache.of(() -> SystemProperty.get("gate.auth.oidc.client_id")
		.orElseThrow(() -> new AuthenticatorException("Missing gate.auth.oidc.client_id system property")));

	private final Cache<String> clientSecret = Cache.of(() -> SystemProperty.get("gate.auth.oidc.client_secret")
		.orElseThrow(() -> new AuthenticatorException("Missing gate.auth.oidc.client_secret system property")));

	private final Cache<String> redirectUri = Cache.of(() -> SystemProperty.get("gate.auth.oidc.redirect_uri")
		.orElseThrow(() -> new AuthenticatorException("Missing gate.auth.oidc.redirect_uri system property")));

	private final Cache<String> scope = Cache
		.of(() -> SystemProperty.get("gate.auth.oidc.scope").orElse("openid email profile"));

	private final Cache<String> configurationEndpoint = Cache
		.of(() -> SystemProperty.get("gate.auth.oidc.configuration_endpoint")
		.orElseGet(() -> provider.get() + "/.well-known/openid-configuration"));

	private final Cache<String> userId = Cache.of(() -> SystemProperty.get("gate.auth.oidc.user_id").orElse("email"));

	private final Cache<JsonObject> configuration = Cache.of(() ->
	{
		try
		{
			return new URL(configurationEndpoint.get())
				.get()
				.readJsonObject()
				.orElseThrow(
					() -> new AuthenticatorException("Error trying to get configuration from auth provider"));
		} catch (IOException ex)
		{
			throw new AuthenticatorException(ex);
		}
	});

	private final Cache<String> authorizationEndpoint = Cache.of(() -> SystemProperty
		.get("gate.auth.oidc.authorization_endpoint")
		.orElseGet(() -> configuration.get().getString("authorization_endpoint").orElseThrow(
		() -> new AuthenticatorException("Error trying to get authorization_endpoint from provider"))));

	private final Cache<String> tokenEndpoint = Cache.of(() -> SystemProperty.get("gate.auth.oidc.token_endpoint")
		.orElseGet(() -> configuration.get().getString("token_endpoint").orElseThrow(
		() -> new AuthenticatorException("Error trying to get token_endpoint from provider"))));

	private final Cache<String> userInfoEndpoint = Cache.of(() -> SystemProperty.get("gate.auth.oidc.userinfo_endpoint")
		.orElseGet(() -> configuration.get().getString("userinfo_endpoint").orElseThrow(
		() -> new AuthenticatorException("Error trying to get userinfo_endpoint from provider"))));

	private final Cache<String> jwksUri = Cache.of(() -> SystemProperty.get("gate.auth.oidc.jwks_uri")
		.orElseGet(() -> configuration.get().getString("jwks_uri")
		.orElseThrow(() -> new AuthenticatorException("Error trying to get jwks_uri from provider"))));

	private final Cache<String> logoutUri = Cache
		.of(() -> SystemProperty.get("gate.auth.oidc.logout_uri").orElse(null));

	private final Cache<PublicKey> publicKey = Cache.of(() ->
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

			BigInteger modulos = object.getString("n")
				.map(Base64.getUrlDecoder()::decode)
				.map(e -> new BigInteger(1, e))
				.orElseThrow(() -> new AuthenticatorException(
				"Error trying to get public key modulus from auth provider"));

			BigInteger exponent = object.getString("e")
				.map(Base64.getUrlDecoder()::decode)
				.map(e -> new BigInteger(1, e))
				.orElseThrow(() -> new AuthenticatorException(
				"Error trying to get public key exponent from auth provider"));

			RSAPublicKeySpec spec = new RSAPublicKeySpec(modulos, exponent);
			return KeyFactory.getInstance("RSA").generatePublic(spec);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException ex)
		{
			throw new AuthenticatorException(ex);
		}
	});

	private OIDCAuthenticator(GateControl control)
	{
		this.control = control;
	}

	public static OIDCAuthenticator of(GateControl control)
	{
		return new OIDCAuthenticator(control);
	}

	@Override
	public String provider(ScreenServletRequest request, HttpServletResponse response)
	{
		String session = SESSIONS.create();
		return new URL(authorizationEndpoint.get())
			.setParameter("response_type", "code")
			.setParameter("client_id", clientId.get())
			.setParameter("redirect_uri", redirectUri.get())
			.setParameter("scope", scope.get())
			.setParameter("state", session)
			.toString();
	}

	@Override
	public User authenticate(ScreenServletRequest request, HttpServletResponse response)
		throws InvalidPasswordException, InvalidUsernameException, HierarchyException, DefaultPasswordException,
		BadRequestException
	{
		try
		{

			JsonObject tokens = getTokens(request);

			if (tokens.containsKey("id_token"))
			{
				Jwt<Header, Claims> jwt = tokens.getString("id_token")
					.map(Jwts.parserBuilder().setSigningKey(publicKey.get()).build()::parse)
					.orElseThrow(() -> new AuthenticatorException("Error trying to get id token from auth provider"));

				if (jwt.getBody().containsKey(userId.get()))
					return control.select((String) jwt.getBody().get("email", String.class));
			}

			String accessToken = tokens
				.getString("access_token")
				.orElseThrow(() -> new AuthenticatorException("Error trying to get access token from auth provider"));

			JsonObject userInfo = new URL(userInfoEndpoint.get())
				.setCredentials(accessToken)
				.get()
				.readJsonObject()
				.orElseThrow(() -> new AuthenticatorException("Error trying to get user info from auth provider"));

			return control.select(userInfo.getString(userId.get())
				.orElseThrow(() -> new AuthenticatorException("Error trying to get user user id from auth provider")));

		} catch (IOException | AuthenticationException ex)
		{
			throw new BadRequestException();
		}
	}

	@Override
	public String logoutUri(gate.http.ScreenServletRequest request)
	{
		String url = logoutUri.get();
		if (url == null)
			return null;

		return new URL(url)
			.setParameter("client_id", clientId.get())
			.setParameter("post_logout_redirect_uri", request.getRequestURL().toString())
			.toString();
	}

	private JsonObject getTokens(ScreenServletRequest request)
		throws IOException, BadRequestException, AuthenticationException
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
					.set("scope", scope.get())
					.set("client_id", clientId.get())
					.set("client_secret", clientSecret.get())
					.set("redirect_uri", redirectUri.get()))
				.readJsonObject()
				.orElseThrow(() -> new AuthenticatorException("Error trying to get token from auth provider"));
		}

		Authorization authorization = request.getAuthorization()
			.orElseThrow(() -> new AuthenticatorException("Error trying to get token from auth provider"));

		if (authorization instanceof BasicAuthorization)
			return new URL(tokenEndpoint.get())
				.post(new Parameters()
					.set("grant_type", "password")
					.set("scope", scope.get())
					.set("client_id", clientId.get())
					.set("client_secret", clientSecret.get())
					.set("username", ((BasicAuthorization) authorization).username())
					.set("password", ((BasicAuthorization) authorization).password()))
				.readJsonObject()
				.orElseThrow(() -> new AuthenticatorException("Error trying to get token from auth provider"));

		if (authorization instanceof BearerAuthorization)
			return new JsonObject()
				.setString("access_token", ((BearerAuthorization) authorization).token());

		throw new BadRequestException();
	}
}
