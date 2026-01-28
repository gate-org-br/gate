package gate;

import java.lang.reflect.Method;

import gate.annotation.Annotations;
import gate.annotation.Authorization;
import gate.annotation.Disabled;
import gate.annotation.Public;
import gate.annotation.Security;
import gate.annotation.Superuser;
import gate.entity.User;
import gate.type.RequestCommand;

public record AccessRule(
	Security.Type accessType,
	boolean disabled,
	boolean isPublic,
	boolean superuser,
	Authorization.Value authorization)
	{

	public static AccessRule of(Class<? extends Object> type,
		Method method, RequestCommand command)
	{
		var disabled = Annotations.exists(Disabled.class, type, method);
		var isPublic = Annotations.exists(Public.class, type, method);
		var superuser = Annotations.exists(Superuser.class, type, method);
		var accessType = Security.Extractor.extract(method).orElse(Security.Type.AUTHORIZATION);

		Authorization.Value authorization = Authorization.Extractor.extract(method,
			command.module(), command.screen(), command.action());
		return new AccessRule(accessType, disabled, isPublic, superuser, authorization);
	}

	public boolean allows(User user)
	{
		if (disabled)
			return false;

		if (isPublic)
			return true;

		if (superuser)
			return user != null && user.isSuperUser();

		return switch (accessType)
		{
			case NONE ->
				true;

			case AUTHENTICATION ->
				user != null && user.getId() != null;

			case AUTHORIZATION ->
				user != null
				&& user.getId() != null
				&& user.checkAccess(
				authorization.module(),
				authorization.screen(),
				authorization.action());

			case SPECIFIC_AUTHORIZATION ->
				user != null
				&& user.getId() != null
				&& user.checkSpecificAccess(
				authorization.module(),
				authorization.screen(),
				authorization.action());

			case SUPERUSER ->
				user != null && user.isSuperUser();
		};
	}
}
