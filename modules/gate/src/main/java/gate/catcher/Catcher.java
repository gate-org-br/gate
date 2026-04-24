package gate.catcher;

import gate.http.ScreenServletRequest;
import gate.http.ScreenServletResponse;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

public interface Catcher
{

	void catches(ScreenServletRequest request,
	             ScreenServletResponse response, Throwable exception);

	static Class<? extends Catcher> getCatcher(Class<?> type)
	{
		Map<Class<?>, Class<? extends Catcher>> registry = Registry.INSTANCE;
		for (Class<?> clazz = type; clazz != null; clazz = clazz.getSuperclass())
		{
			if (registry.containsKey(clazz))
				return registry.get(clazz);
			if (clazz.isAnnotationPresent(gate.annotation.Catcher.class))
				return clazz.getAnnotation(gate.annotation.Catcher.class).value();
		}
		return ThrowableCatcher.class;
	}

	final class Registry
	{

		static final Map<Class<?>, Class<? extends Catcher>> INSTANCE = load();

		private static Map<Class<?>, Class<? extends Catcher>> load()
		{
			Map<Class<?>, Class<? extends Catcher>> registry = new ConcurrentHashMap<>();
			ServiceLoader.load(CatcherRegistrar.class).forEach(r -> r.register(registry));
			return registry;
		}
	}
}