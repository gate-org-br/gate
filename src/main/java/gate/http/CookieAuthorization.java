package gate.http;

public class CookieAuthorization implements Authorization
{

	private final String token;

	private CookieAuthorization(String token)
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

	public static CookieAuthorization valueOf(String string)
	{
		return new CookieAuthorization(string);
	}
}