package gate.http;

public class BearerAuthorization implements Authorization
{

	private final String token;

	public BearerAuthorization(String token)
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
		return "Bearer " + token;
	}
}
