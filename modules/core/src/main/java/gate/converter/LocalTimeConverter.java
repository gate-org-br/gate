package gate.converter;

import gate.constraint.Constraint;
import gate.constraint.Maxlength;
import gate.constraint.Pattern;
import gate.error.ConversionException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.List;

public class LocalTimeConverter implements Converter
{

	private static final DateTimeFormatter FORMATTTER
		= DateTimeFormatter.ofPattern("HH:mm");

	private static final List<Constraint.Implementation<?>> CONSTRAINTS
		= Arrays.asList(new Maxlength.Implementation(8),
			new Pattern.Implementation("^[0-9]{2}[:][0-9]{2}$"));

	@Override
	public String getDescription()
	{
		return "Campos de hora devem ser preenchidos no formato HH:MM:SS";
	}

	@Override
	public String getMask()
	{
		return "##:##";
	}

	@Override
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
	public String render(Class<?> type, Object object)
	{
		return object != null ? FORMATTTER.format((TemporalAccessor) object) : "";
	}

	@Override
	public String render(Class<?> type, Object object, String format)
	{
		return object != null ? DateTimeFormatter.ofPattern(format).format((TemporalAccessor) object) : "";
	}

	@Override
	public Object ofString(Class<?> type, String string) throws ConversionException
	{
		if (string == null)
			return null;

		string = string.trim();
		if (string.isEmpty())
			return null;

		try
		{
			return LocalTime.parse(string, FORMATTTER);
		} catch (DateTimeParseException ex)
		{
			throw new ConversionException(ex,
				"%s não é uma hora válida.%n%s.",
				ex.getParsedString(),
				getDescription());
		}
	}

}
