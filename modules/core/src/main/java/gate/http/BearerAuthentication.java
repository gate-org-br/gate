package gate.http;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BearerAuthentication implements Authentication
{

	private final String token;
	private static final Pattern AUTHORIZATION = Pattern.compile("^Bearer ([^ ]+)$", Pattern.CASE_INSENSITIVE);

	/**
	 * Creates a new BearerAuthentication instance.
	 *
	 * @param token the bearer token
	 * @throws IllegalArgumentException if token is null or empty
	 */
	private BearerAuthentication(String token)
	{
		if (token == null || token.trim().isEmpty())
			throw new IllegalArgumentException("Creadentials cannot be null or empty");
		this.token = token;
	}

	/**
	 * @return the bearer token
	 */
	@Override
	public String token()
	{
		return token;
	}

	@Override public Type type() {return Type.BEARER;}

	/**
	 * Creates a Bearer Authentication header string.
	 *
	 * @return the Bearer Authentication header string
	 */
	@Override
	public String toString()
	{
		return "Bearer " + token;
	}

	/**
	 * Creates a BearerAuthentication instance from an authorization header string.
	 *
	 * @param authString the authorization header string
	 * @return a new BearerAuthentication instance
	 * @throws IllegalArgumentException if the string is not a valid Bearer Authentication header
	 */
	public static BearerAuthentication valueOf(String authString)
	{
		if (authString == null)
			throw new IllegalArgumentException("Authentication header can't be null");

		Matcher matcher = AUTHORIZATION.matcher(authString);
		if (!matcher.matches())
			throw new IllegalArgumentException("Invalid Authentication header format");
		return new BearerAuthentication(matcher.group(1));
	}

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