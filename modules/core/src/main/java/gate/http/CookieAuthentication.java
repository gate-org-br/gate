package gate.http;

public class CookieAuthentication implements Authentication
{

	private final String token;

	private CookieAuthentication(String token)
	{
		this.token = token;
	}

	@Override
	public String token()
	{
		return token;
	}

	@Override public Type type() {return Type.COOKIE;}

	@Override
	public String toString()
	{
		return token;
	}

	public static CookieAuthentication valueOf(String string)
	{
		return new CookieAuthentication(string);
	}
}