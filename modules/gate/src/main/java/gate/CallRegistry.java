package gate;

import gate.annotation.Asynchronous;
import gate.annotation.Cors;
import gate.annotation.HttpMethod;
import gate.annotation.MainAction;
import gate.base.Screen;
import gate.entity.User;
import gate.type.RequestCommand;
import jakarta.enterprise.context.ApplicationScoped;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class CallRegistry
{
	private Call mainAction;
	private static final String ANY = "ANY";
	private final Map<String, Map<RequestCommand, Call>> instances = new ConcurrentHashMap<>();

	public boolean contains(RequestCommand command)
	{
		return instances.values().stream()
				.anyMatch(e -> e.containsKey(command));
	}

	public Optional<Call> get(String method, RequestCommand command)
	{
		method = method != null ? method.toUpperCase() : null;

		Call call = instances.getOrDefault(method, Map.of()).get(command);
		if (call != null)
			return Optional.of(call);

		call = instances.getOrDefault(ANY, Map.of()).get(command);
		if (call != null)
			return Optional.of(call);

		return Optional.empty();
	}

	public Optional<ActionMetadata> getMetadata(RequestCommand command)
	{
		return Optional.ofNullable(instances.getOrDefault("GET", Map.of()).get(command))
				.or(() -> Optional.ofNullable(instances.getOrDefault(ANY, Map.of()).get(command)))
				.map(Call::metadata);
	}

	public Call getMainAction()
	{
		return mainAction;
	}

	public boolean canAccess(User user, RequestCommand command)
	{
		return instances.values().stream()
				.map(Map::values)
				.flatMap(Collection::stream)
				.filter(a -> a.command().matches(command))
				.anyMatch(a -> a.accessRule().allows(user));
	}

	public void register(List<Class<Screen>> screens)
	{
		for (var type : screens)
		{
			String module = type.getPackage().getName();
			String screen = NameResolver.screen(type);

			for (Method method : type.getMethods())
			{
				if (method.getName().startsWith("call"))
				{
					String action = method.getName().substring(4);
					RequestCommand command = new RequestCommand(module, screen, action);
					Collection<String> methods = HttpMethod.Extractor.extract(method);

					var cors = method.isAnnotationPresent(Cors.class);
					var asynchronous = method.isAnnotationPresent(Asynchronous.class);
					var accessRule = AccessRule.of(type, method, command);
					var metadata = ActionMetadata.of(type, method);
					var runtimeAction = new Call(command, type, method,
							methods,
							accessRule,
							cors,
							asynchronous,
							metadata);

					register(new RequestCommand(module, screen, action), runtimeAction);

					if (method.isAnnotationPresent(MainAction.class))
					{
						if (mainAction != null)
							throw new IllegalStateException("Only one @MainAction is allowed");

						mainAction = runtimeAction;
					}
				}
			}
		}
	}

	private void register(RequestCommand command, Call runtimeAction)
	{
		Collection<String> methods = runtimeAction.httpMethods().isEmpty()
				? List.of(ANY)
				: runtimeAction.httpMethods().stream().toList();

		for (String method : methods)
			checkDuplicated(command, method);

		for (String method : methods)
			instances.computeIfAbsent(method, e -> new ConcurrentHashMap<>())
					.put(command, runtimeAction);
	}

	private void checkDuplicated(RequestCommand command, String method)
	{
		if (ANY.equals(method))
		{
			if (instances.values().stream().anyMatch(e -> e.containsKey(command)))
				throw new IllegalStateException("Duplicated action: " + command);
			return;
		}

		if (instances.getOrDefault(ANY, Map.of()).containsKey(command)
				|| instances.getOrDefault(method, Map.of()).containsKey(command))
			throw new IllegalStateException("Duplicated action: " + command);
	}
}