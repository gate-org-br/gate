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
import gate.util.SystemProperty;
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

	private final GateControl control;
	private static final SecuritySessions SESSIONS = SecuritySessions.of(60000);

	private final Cache<String> provider;
	private final Cache<String> clientId;
	private final Cache<String> clientSecret;
	private final Cache<String> redirectUri;
	private final Cache<String> scope;
	private final Cache<String> configurationEndpoint;
	private final Cache<String> userId;
	private final Cache<JsonObject> configuration;
	private final Cache<String> authorizationEndpoint;
	private final Cache<String> tokenEndpoint;
	private final Cache<String> userInfoEndpoint;
	private final Cache<String> jwksUri;
	private final Cache<String> logoutUri;
	private final Cache<PublicKey> publicKey;

	private String getCallback(ScreenServletRequest request)
	{
		return redirectUri.get()
			.replace("${server}", "%s://%s:%d"
				.formatted(request.getScheme(),
					request.getLocalAddr(),
					request.getLocalPort()));
	}

	private OIDCAuthenticator(String app, GateControl control)
	{
		this.control = control;

		clientId = Cache.of(() -> SystemProperty.get(app + ".auth.oidc.client_id")
			.or(() -> SystemProperty.get("gate.auth.oidc.client_id"))
			.orElseThrow(() -> new AuthenticatorException("Missing gate.auth.oidc.client_id system property")));

		clientSecret = Cache.of(() -> SystemProperty.get(app + ".auth.oidc.client_secret")
			.or(() -> SystemProperty.get("gate.auth.oidc.client_secret"))
			.orElseThrow(() -> new AuthenticatorException("Missing gate.auth.oidc.client_id system property")));

		provider = Cache.of(() -> SystemProperty.get(app + ".auth.oidc.provider")
			.or(() -> SystemProperty.get("gate.auth.oidc.provider"))
			.orElseThrow(() -> new AuthenticatorException("Missing gate.auth.oidc.provider system property")));

		configurationEndpoint = Cache
			.of(() -> SystemProperty.get(app + ".auth.oidc.configuration_endpoint")
			.or(() -> SystemProperty.get("gate.auth.oidc.configuration_endpoint"))
			.orElseGet(() -> provider.get() + "/.well-known/openid-configuration"));

		redirectUri = Cache.of(() -> SystemProperty.get(app + ".auth.oidc.redirect_uri")
			.or(() -> SystemProperty.get("gate.auth.oidc.redirect_uri"))
			.orElse("${server}/Gate"));

		userId = Cache.of(() -> SystemProperty.get(app + ".auth.oidc.user_id")
			.or(() -> SystemProperty.get("gate.auth.oidc.user_id"))
			.orElse("email"));

		scope = Cache.of(() -> SystemProperty.get(app + ".auth.oidc.scope")
			.or(() -> SystemProperty.get("gate.auth.oidc.scope"))
			.orElse("openid email profile"));

		configuration = Cache.of(() ->
		{
			try
			{
				return new URL(configurationEndpoint.get())
					.get()
					.readJsonObject()
					.orElseThrow(() -> new AuthenticatorException("Error trying to get configuration from auth provider"));
			} catch (IOException ex)
			{
				throw new AuthenticatorException(ex);
			}
		});

		authorizationEndpoint = Cache.of(() -> SystemProperty.get(app + ".auth.oidc.authorization_endpoint")
			.or(() -> SystemProperty.get("gate.auth.oidc.authorization_endpoint"))
			.orElseGet(() -> configuration.get().getString("authorization_endpoint")
			.orElseThrow(() -> new AuthenticatorException("Error trying to get authorization_endpoint from provider"))));

		tokenEndpoint = Cache.of(() -> SystemProperty.get(app + ".auth.oidc.token_endpoint")
			.or(() -> SystemProperty.get("gate.auth.oidc.token_endpoint"))
			.orElseGet(() -> configuration.get().getString("token_endpoint")
			.orElseThrow(() -> new AuthenticatorException("Error trying to get token_endpoint from provider"))));

		userInfoEndpoint = Cache.of(() -> SystemProperty.get(app + ".auth.oidc.userinfo_endpoint")
			.or(() -> SystemProperty.get("gate.auth.oidc.userinfo_endpoint"))
			.orElseGet(() -> configuration.get().getString("userinfo_endpoint")
			.orElseThrow(() -> new AuthenticatorException("Error trying to get userinfo_endpoint from provider"))));

		jwksUri = Cache.of(() -> SystemProperty.get(app + ".auth.oidc.jwks_uri")
			.or(() -> SystemProperty.get("gate.auth.oidc.jwks_uri"))
			.orElseGet(() -> configuration.get().getString("jwks_uri")
			.orElseThrow(() -> new AuthenticatorException("Error trying to get jwks_uri from provider"))));

		logoutUri = Cache.of(() -> SystemProperty.get(app + ".auth.oidc.logout_uri")
			.or(() -> SystemProperty.get("gate.auth.oidc.logout_uri"))
			.orElse(null));

		publicKey = Cache.of(() ->
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
					.orElseThrow(() -> new AuthenticatorException("Error trying to get public key modulus from auth provider"));

				BigInteger exponent = object.getString("e")
					.map(Base64.getUrlDecoder()::decode)
					.map(e -> new BigInteger(1, e))
					.orElseThrow(() -> new AuthenticatorException("Error trying to get public key exponent from auth provider"));

				RSAPublicKeySpec spec = new RSAPublicKeySpec(modulos, exponent);
				return KeyFactory.getInstance("RSA").generatePublic(spec);
			} catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException ex)
			{
				throw new AuthenticatorException(ex);
			}
		});
	}

	public static OIDCAuthenticator of(String app, GateControl control)
	{
		return new OIDCAuthenticator(app, control);
	}

	@Override
	public String provider(ScreenServletRequest request, HttpServletResponse response)
	{
		String session = SESSIONS.create();

		return new URL(authorizationEndpoint.get())
			.setParameter("response_type", "code")
			.setParameter("client_id", clientId.get())
			.setParameter("redirect_uri", getCallback(request))
			.setParameter("scope", scope.get())
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
	public User authenticate(ScreenServletRequest request, HttpServletResponse response)
		throws HttpException, AuthenticationException, HierarchyException
	{
		try
		{
			if (request.getParameter("code") != null)
				return authorizationCodeFlow(request, response);
			else
				return clientCredentialsCodeFlow(request, response);
		} catch (IOException | RuntimeException ex)
		{
			throw new InternalServerException();
		}
	}

	private User authorizationCodeFlow(ScreenServletRequest request, HttpServletResponse response)
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
				.set("scope", scope.get())
				.set("client_id", clientId.get())
				.set("client_secret", clientSecret.get())
				.set("redirect_uri", getCallback(request)))
			.readJsonObject()
			.orElseThrow(() -> new AuthenticationException("Error trying to get token from auth provider"));

		if (tokens.containsKey("id_token"))
		{
			var jwt = tokens.getString("id_token")
				.map(Jwts.parser().verifyWith(publicKey.get()).build()::parse)
				.orElseThrow(() -> new AuthenticationException("Error trying to get id token from auth provider"));

			if (jwt.getPayload() instanceof Claims claims
				&& claims.containsKey(userId.get()))
				return control.select(claims.get(userId.get(), String.class));
		}

		String accessToken = tokens
			.getString("access_token")
			.orElseThrow(() -> new AuthenticationException("Error trying to get access token from auth provider"));

		JsonObject userInfo = new URL(userInfoEndpoint.get())
			.setCredentials(accessToken)
			.get()
			.readJsonObject()
			.orElseThrow(() -> new AuthenticationException("Error trying to get user info from auth provider"));

		return control.select(userInfo.getString(userId.get())
			.orElseThrow(() -> new AuthenticationException("Error trying to get user user id from auth provider")));
	}

	private User clientCredentialsCodeFlow(ScreenServletRequest request, HttpServletResponse response)
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
		String url = logoutUri.get();
		if (url == null)
			return null;

		return new URL(url)
			.setParameter("client_id", clientId.get())
			.setParameter("post_logout_redirect_uri", request.getRequestURL().toString())
			.toString();
	}

	@Override
	public Type getType()
	{
		return Authenticator.Type.OIDC;
	}
}
