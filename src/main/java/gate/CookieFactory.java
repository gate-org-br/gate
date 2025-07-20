package gate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class CookieFactory
{

	private static final int MAX_AGE = 3600;
	private static final String PATH = "/";

	public static String create(String name, String value)
	{
		return String.format("%s=%s; Max-Age=%d; Path=%s; HttpOnly; SameSite=Lax",
			name, URLEncoder.encode(value, StandardCharsets.UTF_8), MAX_AGE, PATH);
	}

	public static String delete(String name)
	{
		return String.format("%s=; Max-Age=0; Path=%s; HttpOnly; SameSite=Lax", name, PATH);
	}
}
