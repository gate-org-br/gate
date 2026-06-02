package gate.lang.constructionStrategy;

import gate.error.ConstructionException;
import gate.lang.property.Attribute;

import java.lang.reflect.Executable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

record Arguments(List<Attribute> attributes, Object[] values)
{
	static Arguments of(Class<?> type,
	                    Executable executable,
	                    Map<Attribute, Object> attributes)
	{
		var parameters = executable.getParameters();
		var constructorAttributes = Arrays.stream(parameters)
				.map(parameter -> attributes.keySet().stream()
						.filter(attribute -> attribute.matches(parameter))
						.findFirst()
						.orElse(null))
				.toList();

		var values = constructorAttributes.stream()
				.map(attribute -> attribute != null ? attributes.get(attribute) : null)
				.toArray();

		for (var i = 0; i < parameters.length; i++)
			if (values[i] == null && parameters[i].getType().isPrimitive())
				throw new ConstructionException(type, executable, attributes.keySet(), parameters[i]);

		return new Arguments(constructorAttributes, values);
	}

	boolean contains(Attribute attribute) {return attributes.contains(attribute);}
}