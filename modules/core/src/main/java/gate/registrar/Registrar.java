package gate.registrar;

import java.util.Map;

public interface Registrar<T>
{

	void register(Map<Class<?>, T> registry);
}