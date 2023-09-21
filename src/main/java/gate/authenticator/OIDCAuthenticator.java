package gate.authenticator;

import gate.GateControl;
import gate.entity.User;
import gate.error.AuthenticationException;
import gate.error.AuthenticatorException;
import gate.error.BadRequestException;
import gate.error.DefaultPasswordException;
import gate.error.HierarchyException;
import gate.error.InvalidPasswordException;
import gate.error.InvalidUsernameException;
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class OIDCAuthenticator implements Authenticator
{

	private final GateControl control;
	private final String provider;
	private final String clientID;
	private final String clientSecret;
	private final String redirectURI;
	private final String scope;
	private volatile JsonObject configuration;
	private volatile PublicKey publicKey;
	private static final SecuritySessions SESSIONS = SecuritySessions.of(60000);

	public OIDCAuthenticator(GateControl control,
		String provider,
		String clientID,
		String clientSecret,
		String redirectURI,
		String scope)
	{
		this.control = control;
		this.provider = provider;
		this.redirectURI = redirectURI;
		this.clientID = clientID;
		this.clientSecret = clientSecret;
		this.scope = scope;
	}

	@Override
	public String provider(HttpServletRequest request, HttpServletResponse response) throws AuthenticatorException
	{
		try
		{
			JsonObject config = getConfig();
			String authorizationEndpoint = config.getString("authorization_endpoint")
				.orElseThrow(() -> new AuthenticatorException("Error trying to get authorization endpoint from auth provider"));

			String session = SESSIONS.create();
			return new URL(authorizationEndpoint)
				.setParameter("response_type", "code")
				.setParameter("client_id", clientID)
				.setParameter("redirect_uri", redirectURI)
				.setParameter("scope", scope)
				.setParameter("state", session)
				.toString();

		} catch (RuntimeException ex)
		{
			throw new AuthenticatorException(ex);
		}
	}

	public static OIDCAuthenticator of(GateControl control)
	{
		String provider = System.getProperty("gate.auth.oidc.provider", System.getenv("gate.auth.oidc.provider"));
		if (provider == null)
			throw new java.lang.IllegalArgumentException("Missing gate.auth.oidc.provider system property");

		String clientID = System.getProperty("gate.auth.oidc.client_id", System.getenv("gate.auth.oidc.client_id"));
		if (clientID == null)
			throw new java.lang.IllegalArgumentException("Missing gate.auth.oidc.client_id system property");

		String clientSecret = System.getProperty("gate.auth.oidc.client_secret", System.getenv("gate.auth.oidc.client_secret"));
		if (clientSecret == null)
			throw new java.lang.IllegalArgumentException("Missing gate.auth.oidc.client_secret system property");

		String redirectURI = System.getProperty("gate.auth.oidc.redirect_uri", System.getenv("gate.auth.oidc.redirect_uri"));
		if (redirectURI == null)
			throw new java.lang.IllegalArgumentException("Missing gate.auth.oidc.redirect_uri system property");

		String scope = System.getProperty("gate.auth.oidc.scope", System.getenv("gate.auth.oidc.scope"));
		if (scope == null)
			scope = "openid email profile";

		return new OIDCAuthenticator(control, provider, clientID, clientSecret, redirectURI, scope);
	}

	@Override
	public User authenticate(HttpServletRequest request, HttpServletResponse response)
		throws AuthenticatorException, InvalidPasswordException,
		InvalidUsernameException, HierarchyException, DefaultPasswordException,
		AuthenticationException, BadRequestException
	{
		try
		{
			var code = request.getParameter("code");
			var state = request.getParameter("state");
			if (code == null || state == null || !SESSIONS.check(state))
				throw new BadRequestException("Bad request");

			JsonObject config = getConfig();
			String tokenEndpoint = config.getString("token_endpoint").orElseThrow(() -> new AuthenticatorException("Error trying to get token endpoint from auth provider"));
			String userinfoEndpoint = config.getString("userinfo_endpoint").orElseThrow(() -> new AuthenticatorException("Error trying to get user info endpoint from auth provider"));

			JsonObject tokens = new URL(tokenEndpoint)
				.post(new Parameters()
					.set("grant_type", "authorization_code")
					.set("code", code)
					.set("scope", scope)
					.set("client_id", clientID)
					.set("client_secret", clientSecret)
					.set("redirect_uri", redirectURI))
				.readJsonObject()
				.orElseThrow(() -> new AuthenticatorException("Error trying to get access token from auth provider"));

			if (tokens.containsKey("id_token"))
			{
				Jwt<Header, Claims> jwt = tokens.getString("id_token")
					.map(Jwts.parserBuilder()
						.setSigningKey(getPublicKey()).build()::parse)
					.orElseThrow(() -> new AuthenticatorException("Error trying to get id token from auth provider"));

				if (jwt.getBody().containsKey("email"))
					return control.select((String) jwt.getBody().get("email", String.class));
			}

			String accessToken = tokens
				.getString("access_token")
				.orElseThrow(() -> new AuthenticatorException("Error trying to get access token from auth provider"));

			JsonObject userInfo = new URL(userinfoEndpoint)
				.setCredentials(accessToken)
				.get()
				.readJsonObject()
				.orElseThrow(() -> new AuthenticatorException("Error trying to get user info from auth provider"));

			return control.select(userInfo.getString("email").orElseThrow(() -> new AuthenticatorException("Error trying to get user email from auth provider")));

		} catch (IOException | InvalidUsernameException | RuntimeException ex)
		{
			throw new AuthenticatorException(ex);
		}
	}

	private JsonObject getConfig() throws AuthenticatorException
	{
		JsonObject config = this.configuration;
		if (config == null)
		{
			synchronized (this)
			{
				config = this.configuration;
				if (config == null)
				{
					try
					{
						this.configuration = config = new URL(provider + "/.well-known/openid-configuration")
							.get()
							.readJsonObject()
							.orElseThrow(() -> new IllegalArgumentException("Error trying to get configuration from auth provider"));
					} catch (IOException | RuntimeException ex)
					{
						throw new AuthenticatorException(ex);
					}
				}
			}
		}

		return config;
	}

	private PublicKey getPublicKey() throws AuthenticatorException
	{
		PublicKey key = this.publicKey;
		if (key == null)
		{
			synchronized (this)
			{
				key = this.publicKey;
				if (this.publicKey == null)
				{
					try
					{
						String jwksURI = getConfig().getString("jwks_uri").orElseThrow(()
							-> new AuthenticatorException("Error trying to get jwks endpoint from auth provider"));

						JsonObject object = new URL(jwksURI)
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
						this.publicKey = key = KeyFactory.getInstance("RSA").generatePublic(spec);
					} catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException | RuntimeException ex)
					{
						throw new AuthenticatorException(ex);
					}
				}
			}
		}
		return key;
	}
}
