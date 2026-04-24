package gate;

import gate.annotation.*;
import gate.entity.User;
import gate.type.RequestCommand;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.stream.Stream;

public record AccessRule(
		Security.Type accessType,
		Authorization.Value authorization)
{

	public static AccessRule of(Class<? extends Object> type,
								Method method, RequestCommand command)
	{
		Security.Type accessType
				= getAccessType(method)
				.or(() -> getAccessType(type))
				.or(() -> getAccessType(type.getPackage()))
				.orElse(Security.Type.AUTHORIZATION);
		Authorization.Value authorization =
				Authorization.Extractor.extract(method,
						command.module(), command.screen(), command.action());
		return new AccessRule(accessType, authorization);
	}

	private static Optional<Security.Type> getAccessType(AnnotatedElement element)
	{
		if (Stream.of(element.isAnnotationPresent(Disabled.class),
				element.isAnnotationPresent(Public.class),
				element.isAnnotationPresent(Superuser.class),
				element.isAnnotationPresent(Security.class)).filter(e -> e).count() > 1)
			throw new IllegalStateException("Ambiguous access type defined on " + element);

		if (element.isAnnotationPresent(Disabled.class))
			return Optional.of(Security.Type.BLOCK);
		if (element.isAnnotationPresent(Superuser.class))
			return Optional.of(Security.Type.SUPERUSER);
		if (element.isAnnotationPresent(Public.class))
			return Optional.of(Security.Type.NONE);

		if (element.isAnnotationPresent(Security.class))
			return Optional.of(element.getAnnotation(Security.class).value());

		return Optional.empty();
	}

	public boolean allows(User user)
	{
		return switch (accessType)
		{
			case BLOCK -> false;
			case NONE -> true;
			case SUPERUSER -> user != null && user.isSuperUser();
			case AUTHENTICATION -> user != null && user.getId() != null;
			case AUTHORIZATION -> user != null && user.getId() != null
								  && user.checkAccess(authorization.module(), authorization.screen(), authorization.action());
			case SPECIFIC_AUTHORIZATION -> user != null && user.getId() != null
										   && user.checkSpecificAccess(authorization.module(), authorization.screen(), authorization.action());
		};
	}
}