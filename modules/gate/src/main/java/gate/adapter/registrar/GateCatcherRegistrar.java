package gate.adapter.registrar;

import gate.adapter.catcher.AppExceptionCatcher;
import gate.adapter.catcher.Catcher;
import gate.adapter.catcher.CatcherRegistrar;
import gate.error.AppException;

import java.util.HashMap;
import java.util.Map;

public class GateCatcherRegistrar implements CatcherRegistrar
{
	@Override
	public Map<Class<?>, Class<? extends Catcher>> entries()
	{
		Map<Class<?>, Class<? extends Catcher>> registry = new HashMap<>();
		registry.put(AppException.class, AppExceptionCatcher.class);
		return registry;
	}
}