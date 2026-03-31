package gate.thymeleaf;

import jakarta.el.FunctionMapper;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class JaveELFunctionMapper extends FunctionMapper
{

	private final Map<String, Method> functions;

	JaveELFunctionMapper(Map<String, Method> functions)
	{
		this.functions = functions == null ? new HashMap<>() : new HashMap<>(functions);

		for (Method method : TagLib.class.getDeclaredMethods())
			if (Modifier.isStatic(method.getModifiers())
			    && Modifier.isPublic(method.getModifiers()))
				this.functions.put("g:" + method.getName(), method);
	}

	@Override
	public Method resolveFunction(String prefix, String name)
	{
		return functions.get(prefix + ":" + name);
	}

	@Override
	public void mapFunction(String prefix, String localName, Method meth)
	{
		functions.put(prefix + ":" + localName, meth);
	}
}