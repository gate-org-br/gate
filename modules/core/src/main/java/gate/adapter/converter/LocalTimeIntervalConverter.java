package gate.adapter.converter;

import gate.adapter.metadata.Metadata;
import gate.annotation.Description;
import gate.constraint.Constraint;
import gate.constraint.Maxlength;
import gate.constraint.Pattern;
import gate.error.ConversionException;
import gate.type.LocalTimeInterval;
import gate.util.Reflection;

import java.lang.reflect.Type;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Description("Campos de intervalo de hora devem ser preenchidos no formato HH:MM - HH:MM")
public class LocalTimeIntervalConverter implements Converter
{

	private static final List<String> SUFIXES = Arrays.asList("min", "max");
	private static final java.util.regex.Pattern SEPARATOR = java.util.regex.Pattern.compile("\\s+-\\s+");

	public List<Constraint.Implementation<?>> getConstraints()
	{
		List<Constraint.Implementation<?>> constraints = new LinkedList<>();
		constraints.add(new Maxlength.Implementation(19));
		constraints.add(new Pattern.Implementation("^[0-9]{2}:[0-9]{2}(?::[0-9]{2})? - [0-9]{2}:[0-9]{2}(?::[0-9]{2})?$"));
		return constraints;
	}

	@Override
	public Object ofString(Type type, String string) throws ConversionException
	{
		if (string == null)
			return null;
		string = string.trim();
		if (string.isEmpty())
			return null;

		try
		{
			String[] values = SEPARATOR.split(string);
			if (values.length != 2)
				throw new ConversionException(Metadata.getMetadata(Reflection.getRawType(type)).description());
			return LocalTimeInterval.of(Converter.fromString(LocalTime.class, values[0]),
					Converter.fromString(LocalTime.class, values[1]));
		} catch (RuntimeException ex)
		{
			throw new ConversionException(Metadata.getMetadata(Reflection.getRawType(type)).description(), ex);
		}
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? ((LocalTimeInterval) object).toString() : "";
	}

	@Override
	public String toISOString(Class<?> type, Object object)
	{
		if (object == null)
			return "";
		LocalTimeInterval interval = (LocalTimeInterval) object;
		return Converter.toISOString(interval.getMin()) + " - " + Converter.toISOString(interval.getMax());
	}

}