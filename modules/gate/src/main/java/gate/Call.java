package gate;

import java.lang.reflect.Method;
import java.util.Set;

import gate.base.Screen;
import gate.type.RequestCommand;

public record Call(
	RequestCommand command,
	Class<Screen> screen,
	Method method,
	Set<String> httpMethods,
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
