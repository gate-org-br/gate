package gate.http;

public interface Authentication
{
	Type type();

	String token();

	static Authentication valueOf(String string)
	{
		if (string.startsWith("Basic "))
			return BasicAuthentication.valueOf(string);
		else if (string.startsWith("Bearer "))
			return BearerAuthentication.valueOf(string);
		return CookieAuthentication.valueOf(string);
	}

	enum Type
	{
		COOKIE,
		BEARER,
		BASIC
	}
}