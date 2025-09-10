package gate.http;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.StringJoiner;

public class ScreenServletResponse extends HttpServletResponseWrapper
{

	private static final int MAX_AGE = 3600;
	private static final String PATH = "/";
	private static final String SAME_SITE = "Lax";
	public static final String SUBJECT_COOKIE = "subject";

	public ScreenServletResponse(HttpServletResponse response)
	{
		super(response);
	}

	public void createCookie(String name, String path, String sameSite, long maxAge, String value)
	{
		value = URLEncoder.encode(value, StandardCharsets.UTF_8);

		StringJoiner cookie = new StringJoiner("; ");
		cookie.add(name + "=" + value);
		cookie.add("Path=" + path);
		cookie.add("SameSite=" + sameSite);
		cookie.add("Max-Age=" + maxAge);
		cookie.add("HttpOnly");

		addHeader("Set-Cookie", cookie.toString());
	}

	public void createSubjectCookie(String value)
	{
		createCookie(SUBJECT_COOKIE, PATH, SAME_SITE, MAX_AGE, value);
	}

	public void deleteCookie(String name, String path, String sameSite)
	{
		createCookie(name, path, sameSite, 0, "");
	}

	public void deleteSubjectCookie()
	{
		deleteCookie(SUBJECT_COOKIE, PATH, SAME_SITE);
	}

	public void enableCors(String origin)
	{
		setHeader("Access-Control-Allow-Origin", origin);
		setHeader("Access-Control-Max-Age", "3600");
		setHeader("Access-Control-Allow-Credentials", "true");
		setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT, PATCH");
		setHeader("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With, remember-me");
	}
}
