package gate.converter;

import gate.constraint.Constraint;
import gate.constraint.Maxlength;
import gate.constraint.Pattern;
import gate.error.ConversionException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.List;

public class LocalDateTimeConverter implements Converter
{

	private static final DateTimeFormatter FORMATTTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
	private static final List<Constraint.Implementation<?>> CONSTRAINTS = Arrays.asList(new Maxlength.Implementation(19), new Pattern.Implementation("^[0-9]{2}/[0-9]{2}/[0-9]{4} [0-9]{2}:[0-9]{2}$"));

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
		return FORMATTTER.format((TemporalAccessor) object);
	}

	@Override
	public String toText(Class<?> type, Object object)
	{
		return FORMATTTER.format((TemporalAccessor) object);
	}

	@Override
	public String toText(Class<?> type, Object object, String format)
	{
		return DateTimeFormatter.ofPattern(format).format((TemporalAccessor) object);
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
			return LocalDateTime.parse(string, FORMATTTER);
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
		Class<?> type) throws SQLException, ConversionException
	{
		return rs.getObject(index, LocalDateTime.class);
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields,
		Class<?> type) throws SQLException, ConversionException
	{
		return rs.getObject(fields, LocalDateTime.class);
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int index, Object value) throws SQLException,
		ConversionException
	{
		ps.setObject(index, value);
		return index + 1;
	}

}
