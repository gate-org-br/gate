package gate;

import javax.servlet.http.HttpServletRequest;

public class Request
{

	private static final ThreadLocal<HttpServletRequest> current = new ThreadLocal<>();

	static void set(HttpServletRequest request)
	{
		current.set(request);
	}

	public static HttpServletRequest get()
	{
		return current.get();
	}
}
