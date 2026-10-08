package gate.adapter.registrar;

import java.util.Map;

public interface AdapterRegistrar extends Registrar<Object>
{
	@Override
	Map<Class<?>, Object> entries();
}