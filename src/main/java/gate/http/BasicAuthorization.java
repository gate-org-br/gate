package gate.http;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

public class BasicAuthorization implements Authorization
{

	private final String username;
	private final String password;
	private static final String PREFIX = "Basic ";

	private BasicAuthorization(String username, String password)
	{
		if (username == null || username.trim().isEmpty())
			throw new IllegalArgumentException("Username cannot be null or empty");
		if (password == null || password.trim().isEmpty())
			throw new IllegalArgumentException("Password cannot be null or empty");

		this.username = username;
		this.password = password;
	}

	public String username()
	{
		return username;
	}

	public String password()
	{
		return password;
	}

	/**
	 * Creates a Basic Authorization header string.
	 *
	 * @return the Basic Authorization header string
	 */
	@Override
	public String toString()
	{
		String credentials = username + ":" + password;
		byte[] encodedBytes = Base64.getEncoder()
				.encodeToString(credentials.getBytes(StandardCharsets.UTF_8))
				.getBytes(StandardCharsets.UTF_8);
		return PREFIX + new String(encodedBytes, StandardCharsets.UTF_8);
	}

	/**
	 * Creates a BasicAuthorization instance from an authorization header string.
	 *
	 * @param string the authorization header string
	 * @return a new BasicAuthorization instance
	 * @throws IllegalArgumentException if the string is not a valid Basic Authorization header
	 */
	public static BasicAuthorization valueOf(String string)
	{
		if (string == null || !string.startsWith(PREFIX))
			throw new IllegalArgumentException("Invalid Authorization header format");

		String encodedCredentials = string.substring(PREFIX.length());
		String decodedCredentials = new String(Base64.getDecoder().decode(encodedCredentials),
				StandardCharsets.UTF_8);

		String[] values = decodedCredentials.split(":", 2);
		if (values.length != 2)
			throw new IllegalArgumentException("Invalid credentials format");

		return new BasicAuthorization(values[0], values[1]);
	}

	public static BasicAuthorization from(String username, String password)
	{
		return new BasicAuthorization(username, password);
	}

	@Override
	public boolean equals(Object o)
	{
		return o instanceof BasicAuthorization basicAuthorization
				&& Objects.equals(username, basicAuthorization.username)
				&& Objects.equals(password, basicAuthorization.password);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(username, password);
	}
}
