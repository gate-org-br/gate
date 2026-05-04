package gate.adapter.converter;

import gate.adapter.metadata.Metadata;
import gate.annotation.Description;

import gate.constraint.Constraint;
import gate.constraint.Maxlength;
import gate.constraint.Pattern;
import gate.error.ConversionException;
import gate.util.Reflection;

import java.lang.reflect.Type;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.List;

@Description("Campos de hora devem ser preenchidos no formato HH:MM:SS")
public class LocalTimeConverter implements Converter
{

	private static final DateTimeFormatter FORMATTTER
			= DateTimeFormatter.ofPattern("HH:mm");
	private static final java.util.regex.Pattern PATTERN = java.util.regex.Pattern.compile("^[0-9]{2}:[0-9]{2}$");
	private static final java.util.regex.Pattern ISO_PATTERN = java.util.regex.Pattern.compile("^[0-9]{2}:[0-9]{2}(:[0-9]{2})?$");

	private static final List<Constraint.Implementation<?>> CONSTRAINTS
			= Arrays.asList(new Maxlength.Implementation(8),
			new Pattern.Implementation("^[0-9]{2}:[0-9]{2}(?::[0-9]{2})?$"));

	public List<Constraint.Implementation<?>> getConstraints()
	{
		return CONSTRAINTS;
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? FORMATTTER.format((TemporalAccessor) object) : "";
	}

	@Override
	public String toISOString(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
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
			if (PATTERN.matcher(string).matches())
				return LocalTime.parse(string, FORMATTTER);
			else if (ISO_PATTERN.matcher(string).matches())
				return LocalTime.parse(string);
			else
				throw new DateTimeParseException("Invalid time format", string, 0);
		} catch (DateTimeParseException ex)
		{
			throw new ConversionException(ex,
					"%s não é uma hora válida.%n%s.",
					ex.getParsedString(),
						Metadata.getMetadata(Reflection.getRawType(type)).description());
		}
	}

}
