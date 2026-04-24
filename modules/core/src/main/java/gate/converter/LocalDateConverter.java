package gate.converter;

import gate.constraint.Constraint;
import gate.constraint.Maxlength;
import gate.error.ConversionException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class LocalDateConverter implements Converter
{

	private static final Pattern SIMPLE_PATTERN = Pattern.compile("^[0-9]{8}$");
	private static final DateTimeFormatter SIMPLE_FORMATTER = DateTimeFormatter.ofPattern("ddMMyyyy");

	private static final Pattern PATTERN = Pattern.compile("^[0-9]{2}\\/[0-9]{2}\\/[0-9]{4}$");
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	private static final Pattern ISO_PATTERN = Pattern.compile("^[0-9]{4}-[0-9]{2}-[0-9]{2}$");
	private static final DateTimeFormatter IS0_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

	private static final List<Constraint.Implementation<?>> CONSTRAINTS
		= Arrays.asList(new Maxlength.Implementation(10),
			new gate.constraint.Pattern.Implementation("^(?:\\d{8}|\\d{2}\\/\\d{2}\\/\\d{4}|\\d{4}-\\d{2}-\\d{2})$"));

	@Override
	public String getDescription()
	{
		return "Campos de data devem ser preenchidos no formato DD/MM/YYYY";
	}

	@Override
	public String getMask()
	{
		return "##/##/####";
	}

	@Override
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
		return object != null ? IS0_FORMATTER.format((TemporalAccessor) object) : "";
	}

	@Override
	public String render(Class<?> type, Object object)
	{
		return object != null ? FORMATTER.format((TemporalAccessor) object) : "";
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
			if (PATTERN.matcher(string).matches())
				return LocalDate.parse(string, FORMATTER);
			else if (ISO_PATTERN.matcher(string).matches())
				return LocalDate.parse(string, IS0_FORMATTER);
			else if (SIMPLE_PATTERN.matcher(string).matches())
				return LocalDate.parse(string, SIMPLE_FORMATTER);
			else
				throw new DateTimeParseException("Invalid date format", string, 0);
		} catch (DateTimeParseException ex)
		{
			throw new ConversionException(ex,
				"%s não é uma data válida.%n%s.",
				ex.getParsedString(),
				getDescription());
		}
	}

}
