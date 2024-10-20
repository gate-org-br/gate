package gate;

import jakarta.servlet.http.Cookie;

public class CookieFactory
{

	public static Cookie create(String name, String value)
	{
		Cookie cookie = new Cookie(name, value);
		cookie.setMaxAge(3600);
		cookie.setPath("/");
		cookie.setHttpOnly(true);
		return cookie;
	}

	public static Cookie delete(String name)
	{
		Cookie cookie = new Cookie(name, null);
		cookie.setMaxAge(0);
		cookie.setPath("/");
		cookie.setHttpOnly(true);
		return cookie;
	}
}
