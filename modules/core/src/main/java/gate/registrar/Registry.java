package gate.registrar;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class Registry<T>
{
	private final Function<Class<?>, T> extractor;
	private final Function<Class<?>, T> fallback;
	private final Map<Class<?>, T> defaults;

	private final Map<Class<?>, T> instances = new ConcurrentHashMap<>();

	Registry(Map<Class<?>, T> defaults, Function<Class<?>, T> extractor,
	         Function<Class<?>, T> fallback)
	{
		this.extractor = extractor;
		this.fallback = fallback;
		this.defaults = defaults;
	}

	public static <T> Registry<T> create(Class<? extends Registrar<T>> register,
	                                     Function<Class<?>, T> get,
	                                     Function<Class<?>, T> fallback)
	{
		Map<Class<?>, T> targets = new HashMap<>();
		ServiceLoader.load(register)
				.forEach(registrar -> registrar.register(targets));
		return new Registry<>(targets, get, fallback);
	}

	public T get(Class<?> type)
	{
		return instances.computeIfAbsent(Objects.requireNonNull(type), e ->
		{
			for (Class<?> clazz = e; clazz != null; clazz = clazz.getSuperclass())
			{
				T target = extractor.apply(clazz);
				if (target != null)
					return target;

				var candidates = new ArrayList<T>();
				for (Class<?> interfaceClazz : clazz.getInterfaces())
				{
					target = extractor.apply(interfaceClazz);
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

			return fallback.apply(e);
		});
	}
}