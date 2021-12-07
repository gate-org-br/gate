package gate.converter;

import gate.constraint.Constraint;
import gate.constraint.Maxlength;
import gate.error.ConversionException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.List;
import java.util.regex.Pattern;

public class LocalDateTimeConverter implements Converter
{

	private static final Pattern PATTERN = java.util.regex.Pattern.compile("^[0-9]{2}\\/[0-9]{2}\\/[0-9]{4} [0-9]{2}:[0-9]{2}$");
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

	private static final Pattern SIMPLE_PATTERN = java.util.regex.Pattern.compile("^[0-9]{12}$");
	private static final DateTimeFormatter SIMPLE_FORMATTER = DateTimeFormatter.ofPattern("ddMMyyyyHHmm");

	private static final Pattern ISO_PATTERN = java.util.regex.Pattern.compile("^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}$");
	private static final DateTimeFormatter IS0_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

	private static final List<Constraint.Implementation<?>> CONSTRAINTS = List.of(new Maxlength.Implementation(19),
		new gate.constraint.Pattern.Implementation("^[0-9]{12}|[0-9]{2}\\/[0-9]{2}\\/[0-9]{4} [0-9]{2}:[0-9]{2}|[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}$"));

	@Override
	public String getDescription()
	{
		return "Campos de data/hora devem ser preenchidos no formato DD/MM/YYYY HH:MM";
	}

	@Override
	public String getMask()
	{
		return "##/##/#### ##:##";
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
	public String toText(Class<?> type, Object object)
	{
		return object != null ? FORMATTER.format((TemporalAccessor) object) : "";
	}

	@Override
	public String toText(Class<?> type, Object object, String format)
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
				return LocalDateTime.parse(string, FORMATTER);
			else if (ISO_PATTERN.matcher(string).matches())
				return LocalDateTime.parse(string, IS0_FORMATTER);
			else if (SIMPLE_PATTERN.matcher(string).matches())
				return LocalDateTime.parse(string, SIMPLE_FORMATTER);
			else
				throw new DateTimeParseException("Invalid date time format", string, 0);
		} catch (DateTimeParseException ex)
		{
			throw new ConversionException(ex,
				"%s não é uma data/hora válida.%n%s.",
				ex.getParsedString(),
				getDescription());
		}
	}

	@Override
	public Object readFromResultSet(ResultSet rs, int index,
		Class<?> type) throws SQLException
	{
		return rs.getObject(index, LocalDateTime.class);
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields,
		Class<?> type) throws SQLException
	{
		return rs.getObject(fields, LocalDateTime.class);
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int index, Object value) throws SQLException
	{
		ps.setObject(index, value);
		return index + 1;
	}

}
