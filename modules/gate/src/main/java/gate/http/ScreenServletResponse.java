package gate.http;

import gate.util.SystemProperty;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import java.io.IOException;
import java.util.Optional;

public class ScreenServletResponse extends HttpServletResponseWrapper
{
	public static final String NAME = "subject";

	private final boolean SECURE = SystemProperty.get("gate.auth.session.cookie.secure")
			.or(() -> Optional.of("false"))
			.filter(e -> e.equalsIgnoreCase("true")
			             || e.equalsIgnoreCase("false"))
			.map(Boolean::valueOf)
			.orElseThrow(() -> new RuntimeException("Property gate.auth.session.cookie.secure must be a boolean value"));

	private final String SAME_SITE = SystemProperty.get("gate.auth.session.cookie.same-site")
			.or(() -> Optional.of("Lax"))
			.filter(e -> e.equalsIgnoreCase("Strict")
			             || e.equalsIgnoreCase("Lax")
			             || e.equalsIgnoreCase("None"))
			.map(e -> Character.toUpperCase(e.charAt(0)) + e.substring(1))
			.orElseThrow(() -> new RuntimeException("Property gate.auth.session.cookie.same-site must be Strict, Lax or None"));

	public ScreenServletResponse(HttpServletResponse response)
	{
		super(response);
	}

	public void send(String text) throws IOException
	{
		setContentType("text/plain; charset=UTF-8");
		getWriter().write(text);
	}

	public void enableCors(String origin)
	{
		setHeader("Access-Control-Allow-Origin", origin);
		setHeader("Access-Control-Max-Age", "3600");
		setHeader("Access-Control-Allow-Credentials", "true");
		setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT, PATCH");
		setHeader("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With, remember-me");
	}

	public void createSessionCookie(ScreenServletRequest request, String value)
	{
		Cookie cookie = new Cookie(NAME, value);
		cookie.setMaxAge(3600);
		cookie.setSecure(SECURE);
		cookie.setHttpOnly(true);
			cookie.setPath(Optional.ofNullable(request.getContextPath())
					.filter(e -> !e.isBlank())
					.orElse("/"));
		cookie.setAttribute("SameSite", SAME_SITE);
		addCookie(cookie);
	}

	public void revokeSessionCookie(ScreenServletRequest request)
	{
		Cookie cookie = new Cookie(NAME, null);
		cookie.setMaxAge(0);
		cookie.setSecure(SECURE);
		cookie.setHttpOnly(true);
		cookie.setAttribute("SameSite", SAME_SITE);
			cookie.setPath(Optional.ofNullable(request.getContextPath())
					.filter(e -> !e.isBlank())
					.orElse("/"));
		addCookie(cookie);
	}
}
