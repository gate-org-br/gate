package gate.http;

import java.util.Base64;

public class BasicAuthorization implements Authorization
{

	private final String username;
	private final String password;

	public BasicAuthorization(String username, String password)
	{
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

	@Override
	public String toString()
	{
		return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
	}

}
