package gate;

import jakarta.servlet.http.Cookie;

public class CookieFactory
{

	public static Cookie create(String token)
	{
		Cookie cookie = new Cookie("subject", token);
		cookie.setMaxAge(3600);
		cookie.setPath("/");
		cookie.setHttpOnly(true);
		return cookie;
	}
}
