package gate.adapter.registry;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Registry<T>
{
	private final Map<Class<?>, T> defaults;

	private final Map<Class<?>, T> instances = new ConcurrentHashMap<>();

	protected Registry(Map<Class<?>, T> defaults)
	{
		this.defaults = defaults;
	}

	protected abstract T extractor(Class<?> type);

	protected abstract T fallback(Class<?> type);

	public T get(Class<?> type)
	{
		return instances.computeIfAbsent(Objects.requireNonNull(type), e ->
		{
			for (Class<?> clazz = e; clazz != null; clazz = clazz.getSuperclass())
			{
				T target = extractor(clazz);
				if (target != null)
					return target;

				var candidates = new ArrayList<T>();
				for (Class<?> interfaceClazz : clazz.getInterfaces())
				{
					target = extractor(interfaceClazz);
					if (target == null)
						target = defaults.get(interfaceClazz);
					if (target != null)
						candidates.add(target);
				}

				if (candidates.size() == 1)
					return candidates.get(0);

				target = defaults.get(clazz);
				if (target != null)
					return target;
			}

			return fallback(e);
		});
	}
}