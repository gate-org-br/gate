package gate.adapter.converter;

import gate.constraint.Constraint;
import gate.error.ConversionException;
import gate.util.Reflection;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class EnumConverter implements Converter
{
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return Collections.emptyList();
	}

	@Override
	public Object ofString(Type type, String string) throws ConversionException
	{
		var value = string != null ? string.trim() : null;
		if (value == null || value.isEmpty())
			return null;

		var constantes = Reflection.getRawType(type).getEnumConstants();
		if (value.chars().allMatch(Character::isDigit))
			return constantes[Integer.parseInt(value)];

		return Stream.of(constantes)
				.map(Enum.class::cast)
				.filter(e -> e.name().equals(value))
				.findAny()
				.orElseThrow(() -> new ConversionException(string + " is not a valid enum value"));
	}

	@Override
	public String toString(Class<?> type, Object object) {return object instanceof Enum<?> e ? e.name() : "";}

}
