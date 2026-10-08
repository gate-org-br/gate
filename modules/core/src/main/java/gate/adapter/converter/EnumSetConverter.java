package gate.adapter.converter;

import gate.annotation.Description;

import gate.constraint.Constraint;
import gate.error.ConversionException;
import gate.lang.json.JsonArray;
import gate.lang.json.JsonString;
import gate.util.Reflection;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@Description("Lista de opções")
public class EnumSetConverter implements Converter
{
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return Collections.emptyList();
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		if (object == null)
			return "";
		return ((EnumSet<?>) object).stream().map(e -> e.name()).map(e -> '"' + e + '"')
				.collect(() -> new StringJoiner(", ", "[", "]"), StringJoiner::add,
						StringJoiner::merge)
				.toString();
	}

	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public Object ofString(Type type, String string) throws ConversionException
	{
		if (string == null || string.isBlank())
			return null;

		var elementType = Reflection.getElementGenericType(type);
		var enumClass = (Class<Enum>) (elementType != null
				? Reflection.getRawType(elementType)
				: Reflection.getRawType(type));
		return JsonArray.parse(string).stream().map(e -> (JsonString) e).map(e -> e.getValue())
				.map(e -> Enum.valueOf(enumClass, e))
				.collect(Collectors.toCollection(() -> EnumSet.noneOf(enumClass)));
	}

}