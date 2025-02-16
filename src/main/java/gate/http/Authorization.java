package gate.http;

public interface Authorization
{

	public static Authorization valueOf(String string)
	{
		if (string.startsWith("Basic "))
			return BasicAuthorization.valueOf(string);
		else if (string.startsWith("Bearer "))
			return BearerAuthorization.valueOf(string);
		return CookieAuthorization.valueOf(string);
	}
}
