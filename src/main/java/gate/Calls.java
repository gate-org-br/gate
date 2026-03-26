package gate;

import gate.annotation.AllowMethod;
import gate.annotation.Asynchronous;
import gate.annotation.Cors;
import gate.annotation.MainAction;
import gate.base.Screen;
import gate.entity.User;
import gate.type.RequestCommand;
import jakarta.enterprise.context.ApplicationScoped;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class Calls
{

	private Call mainAction;
	private final Map<RequestCommand, Call> instances = new HashMap<>();

	public Optional<Call> get(RequestCommand command)
	{
		return Optional.ofNullable(instances.get(command));
	}

	public Call getMainAction()
	{
		return mainAction;
	}

	public boolean canAccess(User user, RequestCommand command)
	{
		return instances.values().stream()
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
					Set<String> methods = Stream.of(method.getAnnotationsByType(AllowMethod.class))
							.map(e -> e.value().name()).collect(Collectors.toUnmodifiableSet());

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
		if (instances.containsKey(command))
			throw new IllegalStateException("Duplicated action: " + command);
		instances.put(command, runtimeAction);
	}
}