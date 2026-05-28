package gate.adapter.converter;

import gate.adapter.metadata.Metadata;
import gate.annotation.Description;

import gate.constraint.Constraint;
import gate.constraint.Maxlength;
import gate.error.ConversionException;
import gate.util.Reflection;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.List;
import java.util.regex.Pattern;

@Description("Campos de data/hora devem ser preenchidos no formato DD/MM/YYYY HH:MM")
public class LocalDateTimeConverter implements Converter
{

	private static final Pattern PATTERN = java.util.regex.Pattern.compile("^[0-9]{2}/[0-9]{2}/[0-9]{4} [0-9]{2}:[0-9]{2}$");
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

	private static final Pattern SIMPLE_PATTERN = java.util.regex.Pattern.compile("^[0-9]{12}$");
	private static final DateTimeFormatter SIMPLE_FORMATTER = DateTimeFormatter.ofPattern("ddMMyyyyHHmm");

	private static final Pattern ISO_PATTERN = java.util.regex.Pattern.compile("^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}(:[0-9]{2})?$");
	private static final DateTimeFormatter IS0_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

	private static final List<Constraint.Implementation<?>> CONSTRAINTS = List.of(new Maxlength.Implementation(19),
			new gate.constraint.Pattern.Implementation("^(?:[0-9]{12}|[0-9]{2}/[0-9]{2}/[0-9]{4} [0-9]{2}:[0-9]{2}|[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}(?::[0-9]{2})?)$"));

	public List<Constraint.Implementation<?>> getConstraints()
	{
		return CONSTRAINTS;
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? FORMATTER.format((TemporalAccessor) object) : "";
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
				return LocalDateTime.parse(string, FORMATTER);
			else if (ISO_PATTERN.matcher(string).matches())
				return LocalDateTime.parse(string, IS0_FORMATTER);
			else if (SIMPLE_PATTERN.matcher(string).matches())
				return LocalDateTime.parse(string, SIMPLE_FORMATTER);
			else
				throw new DateTimeParseException("Invalid date time format", string, 0);
		} catch (DateTimeParseException ex)
		{
			throw new ConversionException(
					"%s não é uma data/hora válida.%n%s.".formatted(ex.getParsedString(),
							Metadata.getMetadata(Reflection.getRawType(type)).description()), ex);
		}
	}

}