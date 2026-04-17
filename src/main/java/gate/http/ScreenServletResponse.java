package gate.http;

import gate.security.Credentials;
import gate.util.SystemProperty;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import java.util.Optional;

public class ScreenServletResponse extends HttpServletResponseWrapper
{
	public ScreenServletResponse(HttpServletResponse response)
	{
		super(response);
	}

	public static final String SUBJECT = "subject";

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

	public void createSessionCookie(HttpServletRequest request, String value)
	{
		addCookie(request, SUBJECT,
				Math.min(Credentials.IDLE_TIMEOUT.toSeconds(),
						Credentials.TIMEOUT.toSeconds()), value);
	}

	public void revokeSessionCookie(HttpServletRequest request)
	{
		addCookie(request, SUBJECT, 0, null);
	}

	public void addCookie(HttpServletRequest request, String name, long maxAge, String value)
	{
		Cookie cookie = new Cookie(name, value);
		cookie.setMaxAge((int) maxAge);
		cookie.setSecure(SECURE);
		cookie.setHttpOnly(true);
		cookie.setAttribute("SameSite", SAME_SITE);
		cookie.setPath(Optional.ofNullable(request.getServletContext().getContextPath())
				.filter(e -> !e.isBlank())
				.orElse("/"));
		addCookie(cookie);
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