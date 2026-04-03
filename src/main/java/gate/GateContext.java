package gate;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class GateContext
{

	private static final ThreadLocal<Map<Class<?>, Object>> CURRENT = ThreadLocal.withInitial(HashMap::new);

	public GateContext() {}

	public void set(Map<Class<?>, Object> map) {CURRENT.set(new HashMap<>(map));}

	public <T> void set(Class<T> type, T value)
	{
		if (value == null)
			remove(type);
		else
			CURRENT.get().put(type, value);
	}

	public <T> Optional<T> get(Class<T> type) {return Optional.ofNullable(CURRENT.get().get(type)).map(type::cast);}

	public boolean has(Class<?> type) {return CURRENT.get().containsKey(type);}

	public void remove(Class<?> type) {CURRENT.get().remove(type);}

	public void clear() {CURRENT.remove();}

	public Map<Class<?>, Object> get() {return Map.copyOf(CURRENT.get());}
}
