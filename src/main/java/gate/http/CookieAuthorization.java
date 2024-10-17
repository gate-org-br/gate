package gate.http;

public class CookieAuthorization implements Authorization
{

	private final String token;

	public CookieAuthorization(String token)
	{
		this.token = token;
	}

	public String token()
	{
		return token;
	}

	@Override
	public String toString()
	{
		return token;
	}
}
