package gate;

import gate.base.Screen;
import gate.type.RequestCommand;

import java.lang.reflect.Method;
import java.util.Collection;

public record Call(
		RequestCommand command,
		Class<Screen> screen,
		Method method,
		Collection<String> httpMethods,
		AccessRule accessRule,
		boolean cors,
		boolean asynchronous,
		ActionMetadata metadata)
{

	public boolean allowsHttpMethod(String method)
	{
		return httpMethods.isEmpty()
		       || httpMethods.contains(method);
	}
}