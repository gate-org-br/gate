package gate.adapter.registrar;

import java.util.Map;

public interface Registrar<T>
{
	Map<Class<?>, T> entries();
}