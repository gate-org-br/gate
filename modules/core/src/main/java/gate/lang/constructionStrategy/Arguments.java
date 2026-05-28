package gate.lang.constructionStrategy;

import gate.error.ConstructionException;
import gate.function.TriFunction;
import gate.lang.property.Attribute;

import java.lang.reflect.Executable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class Arguments
{
	Arguments() {}
	
	static List<Attribute> getConstructorAttributes(Executable executable,
	                                                Set<Attribute> attributes)
	{
		return Arrays.stream(executable.getParameters())
				.map(parameter -> attributes.stream()
						.filter(attribute -> attribute.matches(parameter))
						.findFirst()
						.orElse(null))
				.toList();
	}

	static Object[] getArguments(Executable executable,
	                             Map<Attribute, Object> attributes)
	{
		return getConstructorAttributes(executable, attributes.keySet()).stream()
				.map(attribute -> attribute != null ? attributes.get(attribute) : null)
				.toArray();
	}

	static Object[] getArguments(Executable executable,
	                             Map<Attribute, Object> propertyMap,
	                             TriFunction<Attribute, Object, Object, Object> getValue)
	{
		return getConstructorAttributes(executable, propertyMap.keySet()).stream()
				.map(attribute -> attribute != null ? getValue.apply(attribute, null, propertyMap.get(attribute)) : null)
				.toArray();
	}

	static void checkPrimitiveArguments(Class<?> type,
	                                    Executable executable,
	                                    Set<Attribute> attributes,
	                                    Object[] args)
	{
		var parameters = executable.getParameters();
		for (var i = 0; i < parameters.length; i++)
			if (args[i] == null && parameters[i].getType().isPrimitive())
				throw new ConstructionException(type, executable, attributes, parameters[i]);
	}
}