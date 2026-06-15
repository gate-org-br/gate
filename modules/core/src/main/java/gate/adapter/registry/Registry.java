package gate.adapter.registry;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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
				if (target == null)
					target = defaults.get(clazz);
				if (target != null)
					return target;
			}

			for (Class<?> clazz = e; clazz != null; clazz = clazz.getSuperclass())
			{
				T target = getInterfaceTarget(type, Arrays.stream(clazz.getInterfaces())
						.collect(Collectors.toSet()));
				if (target != null)
					return target;
			}

			return fallback(e);
		});
	}

	private T getInterfaceTarget(Class<?> type, Set<Class<?>> interfaces)
	{
		if (interfaces.isEmpty())
			return null;
		var candidates = interfaces.stream().map(e ->
				{
					T target = extractor(e);
					if (target == null)
						target = defaults.get(e);
					return target;
				}).filter(Objects::nonNull)
				.collect(Collectors.toSet());

		return switch (candidates.size())
		{
			case 0 -> getInterfaceTarget(type, interfaces.stream()
					.flatMap(e -> Arrays.stream(e.getInterfaces()))
					.collect(Collectors.toSet()));
			case 1 -> candidates.stream().findFirst().get();
			default -> throw new IllegalStateException("Ambiguous adapters found for %s: %s".formatted(type.getName(),
					candidates));
		};
	}
}