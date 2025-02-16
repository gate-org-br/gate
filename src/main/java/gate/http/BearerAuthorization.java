package gate.http;

import java.util.Objects;

public class BearerAuthorization implements Authorization
{

	private final String token;
	private static final String PREFIX = "Bearer ";

	/**
	 * Creates a new BearerAuthorization instance.
	 *
	 * @param token the bearer token
	 * @throws IllegalArgumentException if token is null or empty
	 */
	private BearerAuthorization(String token)
	{
		if (token == null || token.trim().isEmpty())
			throw new IllegalArgumentException("Token cannot be null or empty");
		this.token = token;
	}

	/**
	 * @return the bearer token
	 */
	public String token()
	{
		return token;
	}

	/**
	 * Creates a Bearer Authorization header string.
	 *
	 * @return the Bearer Authorization header string
	 */
	@Override
	public String toString()
	{
		return PREFIX + token;
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
		if (authString == null || !authString.startsWith(PREFIX))
			throw new IllegalArgumentException("Invalid Authorization header format");
		return new BearerAuthorization(authString.substring(PREFIX.length()));
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
