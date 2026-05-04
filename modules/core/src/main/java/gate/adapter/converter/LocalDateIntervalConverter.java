package gate.adapter.converter;

import gate.adapter.metadata.Metadata;
import gate.annotation.Description;
import gate.constraint.Constraint;
import gate.constraint.Maxlength;
import gate.constraint.Pattern;
import gate.error.ConversionException;
import gate.type.LocalDateInterval;
import gate.util.Reflection;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Description("Campos de intervalo de datas devem ser preenchidos no formato DD/MM/YYYY - DD/MM/YYYY")
public class LocalDateIntervalConverter implements Converter
{

	private static final List<String> SUFIXES = Arrays.asList("min", "max");
	private static final java.util.regex.Pattern SEPARATOR = java.util.regex.Pattern.compile("\\s+-\\s+");

	public List<Constraint.Implementation<?>> getConstraints()
	{
		List<Constraint.Implementation<?>> constraints = new LinkedList<>();
		constraints.add(new Maxlength.Implementation(23));
		constraints.add(new Pattern.Implementation("^(?:[0-9]{2}/[0-9]{2}/[0-9]{4}|[0-9]{4}-[0-9]{2}-[0-9]{2}) - (?:[0-9]{2}/[0-9]{2}/[0-9]{4}|[0-9]{4}-[0-9]{2}-[0-9]{2})$"));
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
			return LocalDateInterval.of(Converter.fromString(java.time.LocalDate.class, values[0]),
					Converter.fromString(java.time.LocalDate.class, values[1]));
		} catch (RuntimeException ex)
		{
			throw new ConversionException(ex, Metadata.getMetadata(Reflection.getRawType(type)).description());
		}
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

	@Override
	public String toISOString(Class<?> type, Object object)
	{
		if (object == null)
			return "";
		LocalDateInterval interval = (LocalDateInterval) object;
		return Converter.toISOString(interval.getMin()) + " - " + Converter.toISOString(interval.getMax());
	}

}
