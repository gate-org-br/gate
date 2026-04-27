package gate.catcher;

import gate.http.ScreenServletRequest;
import gate.http.ScreenServletResponse;
import gate.registrar.Registry;

public interface Catcher
{
	final class Instances
	{
		private static final Registry<Class<? extends Catcher>> CATCHERS = Registry
				.create(CatcherRegistrar.class,
						type -> type.isAnnotationPresent(gate.annotation.Catcher.class)
								? type.getAnnotation(gate.annotation.Catcher.class).value()
								: null, type -> ThrowableCatcher.class);
	}

	void catches(ScreenServletRequest request,
	             ScreenServletResponse response, Throwable exception);

	static Class<? extends Catcher> getCatcher(Class<?> type)
	{
		return Instances.CATCHERS.get(type);
	}
}