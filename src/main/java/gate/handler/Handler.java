package gate.handler;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Handler
{

	/**
	 * Gets the handler associated with the specified java class.
	 *
	 * @param type java class whose associated handler must be returned
	 *
	 * @return the handler associated with the specified java class
	 */
	static Handler getHandler(Class<?> type)
	{
		return Handlers.INSTANCE.get(type);
	}

	static Handler getInstance(Class<? extends Handler> type)
	{
		return Handlers.INSTANCE.getInstance(type);
	}

	public abstract void handle(HttpServletRequest request,
				    HttpServletResponse response, Object value);
}
