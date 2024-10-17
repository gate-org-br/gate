package gate;

import gate.http.ScreenServletRequest;

public class Request
{

	private static final ThreadLocal<ScreenServletRequest> current = new ThreadLocal<>();

	public static void set(ScreenServletRequest request)
	{
		current.set(request);
	}

	public static ScreenServletRequest get()
	{
		return current.get();
	}
}
