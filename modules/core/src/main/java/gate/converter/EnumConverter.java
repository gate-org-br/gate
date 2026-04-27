package gate.converter;

import gate.annotation.Name;
import gate.constraint.Constraint;
import gate.error.ConversionException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class EnumConverter implements Converter
{

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return Collections.emptyList();
	}

	@Override
	public Object ofString(Class<?> type, String string) throws ConversionException
	{
		var value = string != null ? string.trim() : null;
		if (value == null || value.isEmpty())
			return null;

		var constantes = type.getEnumConstants();
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

	@Override
	public String render(Class<?> type, Object object) {return object != null ? Name.Extractor.extract(object).orElse(object.toString()) : "";}

	@Override
	public String render(Class<?> type, Object object, String format) {return object != null ? String.format(format, render(type, object)) : "";}
}