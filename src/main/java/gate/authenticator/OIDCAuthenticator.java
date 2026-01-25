package gate.authenticator;

import java.io.IOException;
import java.security.PublicKey;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import gate.GateControl;
import gate.cache.Cache;
import gate.entity.User;
import gate.error.AuthenticationException;
import gate.error.AuthenticatorException;
import gate.error.HierarchyException;
import gate.error.HttpException;
import gate.http.BasicAuthorization;
import gate.http.BearerAuthorization;
import gate.http.ScreenServletRequest;
import gate.io.URL;
import gate.lang.json.JsonObject;
import gate.security.JWKSPublicKeyParser;
import gate.security.OneTimeTokenStore;
import gate.security.OneTimeValueStore;
import gate.security.PKCE;
import gate.util.Parameters;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletResponse;

public class OIDCAuthenticator implements Authenticator
{

	private final GateControl control;
	private static final String AUTH_FAILED = "Authentication could not be completed";
	private static final OneTimeTokenStore STATES = OneTimeTokenStore.of(60000);
	private static final OneTimeTokenStore NONCES = OneTimeTokenStore.of(60000);
	private static final OneTimeValueStore CODE_VERIFIERS = OneTimeValueStore.of(60000);
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
	private final Cache<String> issuer;
	private final Cache<Map<String, PublicKey>> publicKeys;

	public OIDCAuthenticator(GateControl control, AuthConfig config)
	{
		this.control = control;
		clientId = config.getProperty("oidc.client_id")
			.orElseThrow(() -> new AuthenticatorException("Missing oidc.client_id configuration parameter"));
		clientSecret = config.getProperty("oidc.client_secret").orElse(null);
		provider = config.getProperty("oidc.provider")
			.orElseThrow(() -> new AuthenticatorException("Missing oidc.provider configuration parameter"));
		configurationEndpoint = config.getProperty("oidc.configuration_endpoint")
			.orElse(provider + "/.well-known/openid-configuration");
		redirectUri = config.getProperty("oidc.redirect_uri")
			.orElseThrow(() -> new AuthenticatorException("Missing oidc.redirect_uri configuration parameter"));
		userId = config.getProperty("oidc.user_id").orElse("email");
		scope = config.getProperty("oidc.scope").orElse("openid email profile");
		logoutUri = config.getProperty("oidc.logout_uri").orElse(null);
		configuration = Cache.of(this::fetchConfiguration);
		authorizationEndpoint = Cache.of(() -> config.getProperty("oidc.authorization_endpoint")
			.orElseGet(() -> getEndpoint("authorization_endpoint")));
		tokenEndpoint = Cache
			.of(() -> config.getProperty("oidc.token_endpoint").orElseGet(() -> getEndpoint("token_endpoint")));
		userInfoEndpoint = Cache.of(() -> config.getProperty("oidc.userinfo_endpoint").orElseGet(() -> getEndpoint("userinfo_endpoint")));
		jwksUri = Cache.of(() -> config.getProperty("oidc.jwks_uri").orElseGet(() -> getEndpoint("jwks_uri")));
		issuer = Cache.of(() -> configuration.get().getString("issuer")
			.orElseThrow(() -> new AuthenticatorException("Error trying to get issuer from provider")));
		publicKeys = Cache.of(Duration.ofHours(1), this::fetchPublicKeys);
	}

	@Override
	public String provider(ScreenServletRequest request, HttpServletResponse response)
	{
		String state = STATES.create();
		String nonce = NONCES.create();

		URL authUrl = new URL(authorizationEndpoint.get())
			.setParameter("response_type", "code")
			.setParameter("client_id", clientId)
			.setParameter("redirect_uri", redirectUri)
			.setParameter("scope", scope)
			.setParameter("state", state)
			.setParameter("nonce", nonce);

		if (clientSecret == null)
		{
			var pcke = PKCE.create();
			CODE_VERIFIERS.store(state, pcke.verifier());

			authUrl.setParameter("code_challenge", pcke.challenge())
				.setParameter("code_challenge_method", "S256");
		}

		return authUrl.toString();
	}

	@Override
	public boolean hasCredentials(ScreenServletRequest request) throws AuthenticationException
	{
		return request.getParameter("code") != null
			|| request.getParameter("accessToken") != null
			|| request.getAuthorization() instanceof BasicAuthorization;
	}

	@Override
	public User authenticate(ScreenServletRequest request, HttpServletResponse response)
		throws AuthenticationException, HierarchyException, HttpException
	{
		try
		{
			if (request.getParameter("code") != null)
				return authorizationCodeFlow(control, request);
			else if (request.getParameter("accessToken") != null)
				return partialAuthorizationCodeFlow(control, request);
			else if (request.getAuthorization() instanceof BasicAuthorization basicAuthorization)
				return resourceOwnerPasswordCredentialsFlow(control, basicAuthorization);
			else
				throw new AuthenticationException(AUTH_FAILED);
		} catch (IOException | RuntimeException ex)
		{
			throw new AuthenticationException(AUTH_FAILED);
		}
	}

	private User authorizationCodeFlow(GateControl control, ScreenServletRequest request)
		throws HttpException, AuthenticationException, HierarchyException, IOException
	{
		var code = request.getParameter("code");

		var state = request.getParameter("state");
		if (state == null || !STATES.consume(state))
			return null;

		String codeVerifier = CODE_VERIFIERS.consume(state);

		if (clientSecret == null && codeVerifier == null)
			throw new AuthenticationException(AUTH_FAILED);

		var tokens = new URL(tokenEndpoint.get())
			.post(new Parameters()
				.set("grant_type", "authorization_code")
				.set("code", code)
				.set("scope", scope)
				.set("client_id", clientId)
				.set("client_secret", clientSecret)
				.set("redirect_uri", redirectUri)
				.set("code_verifier", codeVerifier))
			.readJsonObject()
			.orElseThrow(() -> new AuthenticationException(AUTH_FAILED));

		if (tokens.containsKey("id_token"))
		{

			var idToken = tokens.getString("id_token")
				.orElseThrow(() -> new AuthenticationException(AUTH_FAILED));
			Claims claims = getClaims(idToken);
			if (!claims.getAudience()
				.stream().anyMatch(clientId::equals))
				throw new AuthenticationException(AUTH_FAILED);

			if (!NONCES.consume(claims.get("nonce", String.class)))
				throw new AuthenticationException(AUTH_FAILED);

			if (claims.containsKey(userId))
				return control.select(claims.get(userId, String.class));
		}

		String accessToken = tokens.getString("access_token")
			.orElseThrow(() -> new AuthenticationException(AUTH_FAILED));

		JsonObject userInfo = new URL(userInfoEndpoint.get()).setAuthorization(BearerAuthorization.from(accessToken))
			.get().readJsonObject()
			.orElseThrow(() -> new AuthenticationException(AUTH_FAILED));
		return control.select(userInfo.getString(userId)
			.orElseThrow(() -> new AuthenticationException(AUTH_FAILED)));
	}

	private User partialAuthorizationCodeFlow(GateControl control, ScreenServletRequest request)
		throws AuthenticationException, HierarchyException, IOException
	{

		var token = request.getParameter("accessToken");
		if (token.split("\\.").length == 3)
		{
			Claims claims = getClaims(token);
			if (!claims.containsKey(userId))
				throw new AuthenticationException("Invalid token");
			return control.select(claims.get(userId, String.class));
		}

		JsonObject userInfo = new URL(userInfoEndpoint.get()).setAuthorization(BearerAuthorization.from(token)).get()
			.readJsonObject()
			.orElseThrow(() -> new AuthenticationException(AUTH_FAILED));

		return control.select(userInfo.getString(userId)
			.orElseThrow(() -> new AuthenticationException(AUTH_FAILED)));
	}

	private User resourceOwnerPasswordCredentialsFlow(GateControl control, BasicAuthorization basicAuth)
		throws AuthenticationException, HierarchyException, IOException
	{

		JsonObject tokens = new URL(tokenEndpoint.get())
			.post(new Parameters().set("grant_type", "password").set("username", basicAuth.username())
				.set("password", basicAuth.password()).set("client_id", clientId)
				.set("client_secret", clientSecret).set("scope", scope))
			.readJsonObject().orElseThrow(() -> new AuthenticationException(AUTH_FAILED));

		if (tokens.containsKey("id_token"))
		{
			var idToken = tokens.getString("id_token")
				.orElseThrow(() -> new AuthenticationException(AUTH_FAILED));
			Claims claims = getClaims(idToken);
			if (claims.containsKey(userId))
				return control.select(claims.get(userId, String.class));
		}

		String accessToken = tokens.getString("access_token")
			.orElseThrow(() -> new AuthenticationException(AUTH_FAILED));

		JsonObject userInfo = new URL(userInfoEndpoint.get()).setAuthorization(BearerAuthorization.from(accessToken))
			.get().readJsonObject().orElseThrow(() -> new AuthenticationException(AUTH_FAILED));

		return control.select(userInfo.getString(userId)
			.orElseThrow(() -> new AuthenticationException(AUTH_FAILED)));
	}

	@Override
	public User getUser(ScreenServletRequest request) throws AuthenticationException, IOException
	{
		var authorization = request.getAuthorization();
		if (authorization instanceof BearerAuthorization bearerAuthorization)
		{
			if (bearerAuthorization.token().split("\\.").length == 3)
			{
				Claims claims = getClaims(bearerAuthorization.token());
				if (claims.containsKey(userId))
					return control.select(claims.get(userId, String.class));
			} else
			{
				JsonObject userInfo = new URL(userInfoEndpoint.get())
					.setAuthorization(BearerAuthorization.from(bearerAuthorization.token())).get().readJsonObject()
					.orElseThrow(() -> new AuthenticationException("Error retrieving user info"));
				return control.select(userInfo.getString(userId).orElseThrow(
					() -> new AuthenticationException(AUTH_FAILED)));
			}
		} else if (authorization instanceof BasicAuthorization basicAuthorization)
			return resourceOwnerPasswordCredentialsFlow(control, basicAuthorization);

		throw new AuthenticationException(AUTH_FAILED);
	}

	@Override
	public String logoutUri(gate.http.ScreenServletRequest request)
	{
		String url = logoutUri;
		if (url == null)
			return null;

		return new URL(url).setParameter("client_id", clientId)
			.setParameter("post_logout_redirect_uri", request.getRequestURL().toString()).toString();
	}

	private JsonObject fetchConfiguration()
	{
		try
		{
			return new URL(configurationEndpoint).get().readJsonObject().orElseThrow(() -> new AuthenticatorException(AUTH_FAILED));
		} catch (IOException ex)
		{
			throw new AuthenticatorException(ex);
		}
	}

	private Map<String, PublicKey> fetchPublicKeys()
	{
		try
		{
			return new URL(jwksUri.get()).get().readJsonObject().flatMap(e -> e.getJsonArray("keys")).orElseThrow()
				.stream().map(e -> (JsonObject) e)
				.collect(Collectors.toMap(
					e -> e.getString("kid")
						.orElseThrow(() -> new AuthenticatorException(AUTH_FAILED)),
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

	@Override
	public Authenticator.Type getType()
	{
		return Authenticator.Type.OIDC;
	}

	private PublicKey getPublicKey(Header header)
	{
		String kid = (String) header.get("kid");

		if (kid != null)
		{
			PublicKey key = publicKeys.get().get(kid);
			if (key == null)
				throw new AuthenticatorException(AUTH_FAILED);
			return key;
		}

		// Compatiblity for JWT without kid
		if (publicKeys.get().size() == 1)
			return publicKeys.get().values().iterator().next();

		throw new AuthenticatorException(AUTH_FAILED);
	}

	private Claims getClaims(String token)
	{
		var claims = Jwts.parser()
			.keyLocator(this::getPublicKey)
			.build()
			.parseSignedClaims(token)
			.getPayload();
		if (!Objects.equals(claims.getIssuer(), issuer.get()))
			throw new AuthenticatorException(AUTH_FAILED);
		return claims;
	}
}
