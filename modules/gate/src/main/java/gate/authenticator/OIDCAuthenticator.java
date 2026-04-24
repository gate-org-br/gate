package gate.authenticator;

import gate.cache.Cache;
import gate.catalog.UserCatalog;
import gate.entity.User;
import gate.error.*;
import gate.http.BasicAuthorization;
import gate.http.BearerAuthorization;
import gate.http.ScreenServletRequest;
import gate.io.HttpCall;
import gate.io.URLBuilder;
import gate.lang.json.JsonObject;
import gate.security.Crypto;
import gate.security.CryptoKeys;
import gate.security.JWKSPublicKeyParser;
import gate.util.Parameters;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletResponse;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class OIDCAuthenticator implements Authenticator
{
	private static final Duration KEY_TIMEOUT = Duration.ofHours(1);

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
	private final UserCatalog userCatalog;

	public OIDCAuthenticator(AuthConfig config, UserCatalog userCatalog)
	{
		this.userCatalog = userCatalog;
		clientId = config.getProperty("oidc.client_id")
				.orElseThrow(() -> new AuthenticatorException("Missing oidc.client_id configuration parameter"));
		clientSecret = config.getProperty("oidc.client_secret").orElse(null);
		String provider = config.getProperty("oidc.provider")
				.orElseThrow(() -> new AuthenticatorException("Missing oidc.provider configuration parameter"));
		configurationEndpoint = config.getProperty("oidc.configuration_endpoint")
				.orElse(provider + "/.well-known/openid-configuration");

		redirectUri = config.getProperty("oidc.redirect_uri")
				.orElseThrow(() -> new AuthenticatorException("Missing oidc.redirect_uri configuration parameter"));

		userId = config.getProperty("oidc.user_id").orElse("email");
		scope = config.getProperty("oidc.scope").orElse("openid email profile");
		logoutUri = config.getProperty("oidc.logout_uri").orElse(null);
		configuration = Cache.builder(this::fetchConfiguration).build();
		authorizationEndpoint = Cache.builder(() -> config.getProperty("oidc.authorization_endpoint")
				.orElseGet(() -> getEndpoint("authorization_endpoint"))).build();
		tokenEndpoint = Cache
				.builder(() -> config.getProperty("oidc.token_endpoint").orElseGet(() -> getEndpoint("token_endpoint"))).build();
		userInfoEndpoint = Cache.builder(() -> config.getProperty("oidc.userinfo_endpoint")
				.orElseGet(() -> getEndpoint("userinfo_endpoint"))).build();
		jwksUri = Cache.builder(() -> config.getProperty("oidc.jwks_uri").orElseGet(() -> getEndpoint("jwks_uri"))).build();
		issuer = Cache.builder(() -> configuration.get().getString("issuer")
				.orElseThrow(() -> new AuthenticatorException("Error trying to get issuer from provider"))).build();
		publicKeys = Cache.builder(this::fetchPublicKeys).ttl(KEY_TIMEOUT).build();
	}

	@Override
	public String provider(ScreenServletRequest request, HttpServletResponse response)
	{
		var state = State.create();
		return new URLBuilder(authorizationEndpoint.get())
				.setParameter("response_type", "code")
				.setParameter("client_id", clientId)
				.setParameter("redirect_uri", redirectUri)
				.setParameter("scope", scope)
				.setParameter("state", state.format(CryptoKeys.STATE_KEY.get()))
				.setParameter("nonce", state.nonce())
				.setParameter("code_challenge", state.codeChallenge())
				.setParameter("code_challenge_method", "S256")
				.toString();
	}

	@Override
	public boolean hasCredentials(ScreenServletRequest request) throws AuthenticationException
	{
		return request.getParameter("code") != null
		       || request.getParameter("error") != null
		       || request.getAuthorization() instanceof BearerAuthorization
		       || request.getAuthorization() instanceof BasicAuthorization;
	}

	@Override
	public User authenticate(ScreenServletRequest request, HttpServletResponse response)
			throws HierarchyException, HttpException
	{
		if (request.getParameter("error") != null)
		{
			String description = Objects.requireNonNullElse(
					request.getParameter("error_description"),
					request.getParameter("error"));
			throw new AuthenticationException(description);
		}

		try
		{
			if (request.getParameter("code") != null)
				return authorizationCodeFlow(request);
			else if (request.getAuthorization() instanceof BearerAuthorization bearerAuthorization)
				return clientCredentialsFlow(bearerAuthorization);
			else if (request.getAuthorization() instanceof BasicAuthorization basicAuthorization)
				return resourceOwnerPasswordCredentialsFlow(basicAuthorization);
			else
				throw new AuthenticationException("Attempt to authenticate without supplying credentials");
		} catch (IOException ex)
		{
			throw new AuthenticationException(ex);
		} catch (RuntimeException ex)
		{
			throw new InternalServerException(ex);
		}
	}

	private User authorizationCodeFlow(ScreenServletRequest request)
			throws HttpException, HierarchyException, IOException
	{
		var code = request.getParameter("code");

		var state = State.parse(CryptoKeys.STATE_KEY.get(),
				request.getParameter("state"));

		var tokens = HttpCall.post(tokenEndpoint.get())
				.form(new Parameters()
						.put("grant_type", "authorization_code")
						.put("code", code)
						.put("scope", scope)
						.put("client_id", clientId)
						.put("client_secret", clientSecret)
						.put("redirect_uri", redirectUri)
						.put("code_verifier", state.codeVerifier()))
				.execute()
				.readJsonObject()
				.orElseThrow(AuthenticationException::new);

		if (tokens.containsKey("id_token"))
		{

			var idToken = tokens.getString("id_token").orElseThrow(AuthenticationException::new);

			var claims = getClaims(idToken);

			if (claims.getAudience().stream().noneMatch(clientId::equals))
				throw new AuthenticationException();

			if (!this.issuer.get().equals(claims.getIssuer()))
				throw new AuthenticationException();

			if (!state.nonce().equals(claims.get("nonce", String.class)))
				throw new AuthenticationException();

			if (claims.containsKey(userId))
				return userCatalog.select(claims.get(userId, String.class));
		}

		String accessToken = tokens
				.getString("access_token")
				.orElseThrow(AuthenticationException::new);

		JsonObject userInfo = HttpCall.get(userInfoEndpoint.get())
				.authorization(BearerAuthorization.from(accessToken))
				.execute()
				.readJsonObject()
				.orElseThrow(AuthenticationException::new);

		return userCatalog.select(userInfo.getString(userId)
				.orElseThrow(AuthenticationException::new));
	}

	private User clientCredentialsFlow(BearerAuthorization bearerAuthorization)
			throws AuthenticationException, HierarchyException
	{
		String token = bearerAuthorization.token();

		Claims claims = Jwts.parser()
				.keyLocator(this::getPublicKey)
				.build()
				.parseSignedClaims(token)
				.getPayload();

		String issuer = claims.getIssuer();
		if (issuer == null || !issuer.equals(this.issuer.get()))
			throw new AuthenticationException();

		if (claims.getAudience().stream()
				.noneMatch(clientId::equals))
			throw new AuthenticationException();

		String systemId = Optional.ofNullable(claims.get("azp", String.class))
				.or(() -> Optional.ofNullable(claims.getSubject()))
				.orElseThrow(AuthenticationException::new);

		return userCatalog.select(systemId);
	}

	private User resourceOwnerPasswordCredentialsFlow(BasicAuthorization basicAuth)
			throws AuthenticationException, HierarchyException, IOException
	{

		JsonObject tokens = HttpCall.post(tokenEndpoint.get())
				.form(new Parameters()
						.put("grant_type", "password")
						.put("username", basicAuth.username())
						.put("password", basicAuth.password())
						.put("client_id", clientId)
						.put("client_secret", clientSecret)
						.put("scope", scope))
				.execute()
				.readJsonObject()
				.orElseThrow(AuthenticationException::new);

		if (tokens.containsKey("id_token"))
		{
			var idToken = tokens.getString("id_token")
					.orElseThrow(AuthenticationException::new);
			Claims claims = getClaims(idToken);
			if (claims.getAudience().stream().noneMatch(clientId::equals))
				throw new AuthenticationException();
			if (!issuer.get().equals(claims.getIssuer()))
				throw new AuthenticationException();
			if (claims.containsKey(userId))
				return userCatalog.select(claims.get(userId, String.class));
		}

		String accessToken = tokens.getString("access_token")
				.orElseThrow(AuthenticationException::new);

		JsonObject userInfo = HttpCall.get(userInfoEndpoint.get())
				.authorization(BearerAuthorization.from(accessToken))
				.execute()
				.readJsonObject()
				.orElseThrow(AuthenticationException::new);

		return userCatalog.select(userInfo.getString(userId)
				.orElseThrow(AuthenticationException::new));
	}

	@Override
	public String logoutUri(gate.http.ScreenServletRequest request)
	{
		String url = logoutUri;
		if (url == null)
			return null;

		return new URLBuilder(url)
				.setParameter("client_id", clientId)
				.setParameter("post_logout_redirect_uri", request.getRequestURL().toString())
				.toString();
	}

	private JsonObject fetchConfiguration()
	{
		try
		{
			return HttpCall.get(configurationEndpoint)
					.execute()
					.readJsonObject()
					.orElseThrow(AuthenticationException::new);
		} catch (IOException ex)
		{
			throw new AuthenticatorException(ex);
		}
	}

	private Map<String, PublicKey> fetchPublicKeys()
	{
		try
		{
			return HttpCall.get(jwksUri.get())
					.execute()
					.readJsonObject()
					.flatMap(e -> e.getJsonArray("keys"))
					.orElseThrow()
					.stream()
					.map(e -> (JsonObject) e)
					.collect(Collectors.toMap(e -> e.getString("kid")
									.orElseThrow(AuthenticationException::new),
							JWKSPublicKeyParser::parse));
		} catch (IOException ex)
		{
			throw new AuthenticatorException(ex);
		}
	}

	private String getEndpoint(String endpointKey)
	{
		return configuration.get()
				.getString(endpointKey)
				.orElseThrow(AuthenticationException::new);
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
				throw new AuthenticationException();
			return key;
		}

		// Compatiblity for JWT without kid
		if (publicKeys.get().size() == 1)
			return publicKeys.get().values().iterator().next();

		throw new AuthenticationException();
	}

	private Claims getClaims(String token)
	{
		return Jwts.parser()
				.keyLocator(this::getPublicKey)
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	private record State(String nonce, String codeVerifier)
	{

		private static final Duration TIMEOUT = Duration.ofMinutes(10);

		public static State create()
		{
			return new State(
					random(32),
					random(64)
			);
		}

		public String format(SecretKey key)
		{
			Crypto crypto = new Crypto(key);
			var json
					= new JsonObject().setString("nonce", nonce)
					.setString("codeVerifier", codeVerifier)
					.setLong("iat", System.currentTimeMillis());
			return crypto.encryptAndSign(json.toString());
		}

		public String codeChallenge()
		{
			try
			{
				MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
				byte[] hash = sha256.digest(codeVerifier.getBytes(StandardCharsets.US_ASCII));
				return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
			} catch (NoSuchAlgorithmException e)
			{
				throw new InternalServerException(e);
			}
		}

		public static State parse(SecretKey key, String value)
		{
			if (value == null)
				throw new AuthenticationException();

			Crypto crypto = new Crypto(key);
			String plain = crypto.decryptAndVerify(value);

			JsonObject json = JsonObject.parse(plain);

			long now = System.currentTimeMillis();
			long iat = json.getLong("iat").orElseThrow(AuthenticationException::new);
			long skew = Duration.ofMinutes(1).toMillis();
			if (iat > now + skew || now - iat > TIMEOUT.toMillis())
				throw new AuthenticationException();

			var nonce = json.getString("nonce")
					.orElseThrow(AuthenticationException::new);
			var codeVerifier = json.getString("codeVerifier")
					.orElseThrow(AuthenticationException::new);

			return new State(nonce, codeVerifier);
		}

		private static String random(int bytes)
		{
			byte[] b = new byte[bytes];
			new SecureRandom().nextBytes(b);
			return Base64.getUrlEncoder()
					.withoutPadding()
					.encodeToString(b);
		}
	}
}
