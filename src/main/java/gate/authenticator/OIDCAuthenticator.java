package gate.authenticator;

import gate.GateControl;
import gate.cache.Cache;
import gate.entity.User;
import gate.error.AuthenticationException;
import gate.error.AuthenticatorException;
import gate.error.BadRequestException;
import gate.error.HierarchyException;
import gate.error.HttpException;
import gate.error.InternalServerException;
import gate.http.BearerAuthorization;
import gate.http.ScreenServletRequest;
import gate.io.URL;
import gate.lang.json.JsonObject;
import gate.util.Parameters;
import gate.util.SecuritySessions;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

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

	private String getCallback(ScreenServletRequest request)
	{
		return redirectUri.replace("${server}", "%s://%s:%d"
			.formatted(request.getScheme(),
				request.getLocalAddr(),
				request.getLocalPort()));
	}

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
			.setParameter("redirect_uri", getCallback(request))
			.setParameter("scope", scope)
			.setParameter("state", session)
			.toString();
	}

	@Override
	public boolean hasCredentials(ScreenServletRequest request) throws AuthenticationException
	{
		return request.getParameter("code") != null
			|| request.getBearerAuthorization().isPresent();
	}

	@Override
	public User authenticate(GateControl control, ScreenServletRequest request, HttpServletResponse response)
		throws HttpException, AuthenticationException, HierarchyException
	{
		try
		{
			if (request.getParameter("code") != null)
				return authorizationCodeFlow(control, request, response);
			else
				return clientCredentialsCodeFlow(control, request, response);
		} catch (IOException | RuntimeException ex)
		{
			throw new InternalServerException();
		}
	}

	private User authorizationCodeFlow(GateControl control, ScreenServletRequest request, HttpServletResponse response)
		throws HttpException, AuthenticationException, HierarchyException, IOException
	{
		var code = request.getParameter("code");

		var state = request.getParameter("state");
		if (state == null || !SESSIONS.check(state))
			throw new BadRequestException("Error validating state");

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
			var jwt = tokens.getString("id_token")
				.map(Jwts.parser().verifyWith(publicKey.get()).build()::parse)
				.orElseThrow(() -> new AuthenticationException("Error trying to get id token from auth provider"));

			if (jwt.getPayload() instanceof Claims claims
				&& claims.containsKey(userId))
				return control.select(claims.get(userId, String.class));
		}

		String accessToken = tokens
			.getString("access_token")
			.orElseThrow(() -> new AuthenticationException("Error trying to get access token from auth provider"));

		JsonObject userInfo = new URL(userInfoEndpoint.get())
			.setCredentials(accessToken)
			.get()
			.readJsonObject()
			.orElseThrow(() -> new AuthenticationException("Error trying to get user info from auth provider"));

		return control.select(userInfo.getString(userId)
			.orElseThrow(() -> new AuthenticationException("Error trying to get user user id from auth provider")));
	}

	private User clientCredentialsCodeFlow(GateControl control, ScreenServletRequest request, HttpServletResponse response)
		throws HttpException, AuthenticationException, HierarchyException, IOException
	{
		BearerAuthorization authorization = request.getBearerAuthorization()
			.orElseThrow(() -> new BadRequestException("Credentials not supplied"));
		var jwt = Jwts.parser().verifyWith(publicKey.get()).build().parse(authorization.token());

		if (jwt.getPayload() instanceof Claims claims
			&& claims.containsKey("sub"))
			return control.select(claims.get("sub", String.class
			));
		throw new AuthenticationException("Error trying to get subject from auth provider");
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

	@Override
	public Type getType()
	{
		return Authenticator.Type.OIDC;
	}
}
