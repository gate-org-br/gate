package gate.http;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BasicAuthentication implements Authentication
{

	private final String username;
	private final String password;
	private static final Pattern AUTHORIZATION = Pattern.compile("^Basic ([^ ]+)$", Pattern.CASE_INSENSITIVE);

	private BasicAuthentication(String username, String password)
	{
		if (username == null || username.trim().isEmpty())
			throw new IllegalArgumentException("Username cannot be null or empty");
		if (password == null || password.trim().isEmpty())
			throw new IllegalArgumentException("Password cannot be null or empty");

		this.username = username;
		this.password = password;
	}

	@Override public Type type() {return Type.BASIC;}

	@Override public String token() {return null;}

	public String username() {return username;}

	public String password()
	{
		return password;
	}

	/**
	 * Creates a Basic Authentication header string.
	 *
	 * @return the Basic Authentication header string
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
	 * Creates a BasicAuthentication instance from an authorization header string.
	 *
	 * @param string the authorization header string
	 * @return a new BasicAuthentication instance
	 * @throws IllegalArgumentException if the string is not a valid Basic Authentication header
	 */
	public static BasicAuthentication valueOf(String string)
	{
		if (string == null)
			throw new IllegalArgumentException("Authentication header can't be null");
		Matcher matcher = AUTHORIZATION.matcher(string);
		if (!matcher.matches())
			throw new IllegalArgumentException("Invalid Authentication header format");

		String encodedCredentials = matcher.group(1);

		String decodedCredentials = new String(Base64.getDecoder()
				.decode(encodedCredentials),
				StandardCharsets.UTF_8);

		String[] values = decodedCredentials.split(":", 2);
		if (values.length != 2)
			throw new IllegalArgumentException("Invalid credentials format");

		return new BasicAuthentication(values[0], values[1]);
	}

	public static BasicAuthentication of(String username, String password)
	{
		return new BasicAuthentication(username, password);
	}

	@Override
	public boolean equals(Object o)
	{
		return o instanceof BasicAuthentication basicAuthorization
		       && Objects.equals(username, basicAuthorization.username)
		       && Objects.equals(password, basicAuthorization.password);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(username, password);
	}
}