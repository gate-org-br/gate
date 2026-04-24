package gate.http;

public interface Authorization
{
	Type type();

	String token();

	static Authorization valueOf(String string)
	{
		if (string.startsWith("Basic "))
			return BasicAuthorization.valueOf(string);
		else if (string.startsWith("Bearer "))
			return BearerAuthorization.valueOf(string);
		return CookieAuthorization.valueOf(string);
	}

	enum Type
	{
		COOKIE,
		BEARER,
		BASIC
	}
}