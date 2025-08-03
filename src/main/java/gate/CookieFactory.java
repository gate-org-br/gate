package gate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class CookieFactory
{

	private static final int MAX_AGE = 3600;
	private static final String PATH = "/";
	public static final String SUBJECT_COOKIE = "subject";

	public static String create(String value)
	{
		return create(SUBJECT_COOKIE, value);
	}

	public static String delete()
	{
		return delete(SUBJECT_COOKIE);
	}

	public static String create(String cookie, String value)
	{
		return String.format("%s=%s; Max-Age=%d; Path=%s; HttpOnly; SameSite=Lax",
			cookie, URLEncoder.encode(value, StandardCharsets.UTF_8), MAX_AGE, PATH);
	}

	public static String delete(String cookie)
	{
		return String.format("%s=; Max-Age=0; Path=%s; HttpOnly; SameSite=Lax", cookie, PATH);
	}

}
