package gate.http;

import gate.error.InvalidPasswordException;
import gate.error.InvalidUsernameException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BasicAuthorization implements Authorization
{

	private final String username;
	private final String password;
	private static final Pattern AUTHORIZATION = Pattern.compile("^Basic ([^ ]+)$", Pattern.CASE_INSENSITIVE);

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
		return "Basic " + new String(encodedBytes, StandardCharsets.UTF_8);
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
		if (string == null)
			throw new IllegalArgumentException("Authorization header can't be null");
		Matcher matcher = AUTHORIZATION.matcher(string);
		if (!matcher.matches())
			throw new IllegalArgumentException("Invalid Authorization header format");

		String encodedCredentials = matcher.group(1);

		String decodedCredentials = new String(Base64.getDecoder()
				.decode(encodedCredentials),
				StandardCharsets.UTF_8);

		String[] values = decodedCredentials.split(":", 2);
		if (values.length != 2)
			throw new IllegalArgumentException("Invalid credentials format");

		return new BasicAuthorization(values[0], values[1]);
	}

	public static BasicAuthorization from(String username, String password)
	{
		if (username == null || username.isBlank())
			throw new InvalidUsernameException();
		if (password == null || password.isBlank())
			throw new InvalidPasswordException();
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