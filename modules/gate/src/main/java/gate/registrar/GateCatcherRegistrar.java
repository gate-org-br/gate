package gate.registrar;

import gate.catcher.*;
import gate.error.AppException;
import gate.error.ConversionException;

import java.util.Map;

public class GateCatcherRegistrar implements CatcherRegistrar
{

	@Override
	public void register(Map<Class<?>, Class<? extends Catcher>> registry)
	{
		registry.put(AppException.class, AppExceptionCatcher.class);
		registry.put(ConversionException.class, ConversionExceptionCatcher.class);
	}
}
