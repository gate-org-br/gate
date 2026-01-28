package gate;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import gate.annotation.Asynchronous;
import gate.annotation.Cors;
import gate.annotation.MainAction;
import gate.base.Screen;
import gate.entity.User;
import gate.type.RequestCommand;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HEAD;
import jakarta.ws.rs.OPTIONS;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;

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
					Set<String> methods = new HashSet<>();
					if (method.isAnnotationPresent(GET.class))
						methods.add("GET");
					if (method.isAnnotationPresent(POST.class))
						methods.add("POST");
					if (method.isAnnotationPresent(PUT.class))
						methods.add("PUT");
					if (method.isAnnotationPresent(DELETE.class))
						methods.add("DELETE");
					if (method.isAnnotationPresent(PATCH.class))
						methods.add("PATCH");
					if (method.isAnnotationPresent(HEAD.class))
						methods.add("HEAD");
					if (method.isAnnotationPresent(OPTIONS.class))
						methods.add("OPTIONS");
					methods = Collections.unmodifiableSet(methods);

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

					if (instances.containsKey(command))
						throw new IllegalStateException("Duplicated action: " + command);
					instances.put(command, runtimeAction);

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
}
