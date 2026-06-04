package gate.http;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents HTTP Bearer authentication credentials.
 * <p>
 * Instances can be created from a raw token using {@link #of(String)} or parsed from an
 * {@code Authorization} header using {@link #valueOf(String)}. The serialized form follows
 * the standard {@code Bearer <token>} header format.
 */
public class BearerAuthentication implements Authentication
{

	private final String token;
	private static final Pattern AUTHORIZATION = Pattern.compile("^Bearer ([^ ]+)$", Pattern.CASE_INSENSITIVE);

	/**
	 * Creates a new BearerAuthentication instance.
	 *
	 * @param token the bearer token
	 * @throws IllegalArgumentException if the token is null or empty
	 */
	private BearerAuthentication(String token)
	{
		if (token == null || token.trim().isEmpty())
			throw new IllegalArgumentException("Bearer token cannot be null or empty");
		this.token = token;
	}

	/**
	 * Returns the bearer token value without the {@code Bearer} scheme prefix.
	 *
	 * @return the bearer token
	 */
	@Override
	public String token()
	{
		return token;
	}

	/**
	 * Returns the authentication scheme represented by this object.
	 *
	 * @return {@link Type#BEARER}
	 */
	@Override
	public Type type() {return Type.BEARER;}

	/**
	 * Creates a Bearer authentication header string.
	 *
	 * @return the Bearer authentication header string
	 */
	@Override
	public String toString()
	{
		return "Bearer " + token;
	}

	/**
	 * Parses a Bearer authentication header string.
	 * <p>
	 * The scheme is matched case-insensitively, but the header must contain exactly one
	 * space between the {@code Bearer} scheme and the token.
	 *
	 * @param authString the authorization header string
	 * @return a new BearerAuthentication instance
	 * @throws IllegalArgumentException if the header is null or is not a valid Bearer authentication header
	 */
	public static BearerAuthentication valueOf(String authString)
	{
		if (authString == null)
			throw new IllegalArgumentException("Bearer authentication header cannot be null");

		Matcher matcher = AUTHORIZATION.matcher(authString);
		if (!matcher.matches())
			throw new IllegalArgumentException("Invalid Bearer authentication header format: " + authString);
		return new BearerAuthentication(matcher.group(1));
	}

	/**
	 * Creates a Bearer authentication instance from a raw token value.
	 *
	 * @param token the bearer token without the {@code Bearer} scheme prefix
	 * @return a new BearerAuthentication instance
	 * @throws IllegalArgumentException if the token is null or blank
	 */
	public static BearerAuthentication of(String token)
	{
		return new BearerAuthentication(token);
	}

	@Override
	public boolean equals(Object o)
	{
		return o instanceof BearerAuthentication bearerAuthorization
				&& Objects.equals(token, bearerAuthorization.token);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(token);
	}
}