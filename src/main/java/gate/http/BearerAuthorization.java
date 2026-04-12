package gate.http;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BearerAuthorization implements Authorization
{

	private final String token;
	private static final Pattern AUTHORIZATION = Pattern.compile("^Bearer ([^ ]+)$", Pattern.CASE_INSENSITIVE);

	/**
	 * Creates a new BearerAuthorization instance.
	 *
	 * @param token the bearer token
	 * @throws IllegalArgumentException if token is null or empty
	 */
	private BearerAuthorization(String token)
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
	 * Creates a Bearer Authorization header string.
	 *
	 * @return the Bearer Authorization header string
	 */
	@Override
	public String toString()
	{
		return "Bearer " + token;
	}

	/**
	 * Creates a BearerAuthorization instance from an authorization header string.
	 *
	 * @param authString the authorization header string
	 * @return a new BearerAuthorization instance
	 * @throws IllegalArgumentException if the string is not a valid Bearer Authorization header
	 */
	public static BearerAuthorization valueOf(String authString)
	{
		if (authString == null)
			throw new IllegalArgumentException("Authorization header can't be null");

		Matcher matcher = AUTHORIZATION.matcher(authString);
		if (!matcher.matches())
			throw new IllegalArgumentException("Invalid Authorization header format");
		return new BearerAuthorization(matcher.group(1));
	}

	public static BearerAuthorization from(String token)
	{
		return new BearerAuthorization(token);
	}

	@Override
	public boolean equals(Object o)
	{
		return o instanceof BearerAuthorization bearerAuthorization
		       && Objects.equals(token, bearerAuthorization.token);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(token);
	}
}