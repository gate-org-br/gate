package gate.adapter.catcher;

import gate.adapter.registry.CatcherRegistry;
import gate.http.ScreenServletRequest;
import gate.http.ScreenServletResponse;

public interface Catcher
{
	void catches(ScreenServletRequest request,
	             ScreenServletResponse response, Throwable exception);

	static Class<? extends Catcher> getCatcher(Class<?> type)
	{
		return CatcherRegistry.INSTANCE.get(type);
	}
}