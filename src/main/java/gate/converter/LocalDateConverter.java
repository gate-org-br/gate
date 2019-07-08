package gate.converter;

import gate.constraint.Constraint;
import gate.constraint.Maxlength;
import gate.constraint.Pattern;
import gate.error.ConversionException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.List;

public class LocalDateConverter implements Converter
{

	private static final DateTimeFormatter FORMATTTER
			= DateTimeFormatter.ofPattern("dd/MM/yyyy");

	private static final List<Constraint.Implementation<?>> CONSTRAINTS
			= Arrays.asList(new Maxlength.Implementation(10),
					new Pattern.Implementation("^[0-9]{8}|[0-9]{2}[/][0-9]{2}[/][0-9]{4}$"));

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
		return object != null ? FORMATTTER.format((TemporalAccessor) object) : "";
	}

	@Override
	public String toText(Class<?> type, Object object)
	{
		return object != null ?  FORMATTTER.format((TemporalAccessor) object) : "";
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
			return LocalDate.parse(string, FORMATTTER);
		} catch (DateTimeParseException ex)
		{
			throw new ConversionException(ex,
					"%s não é uma data válida.%n%s.",
					ex.getParsedString(),
					getDescription());
		}
	}

	@Override
	public Object readFromResultSet(ResultSet rs, int index,
			Class<?> type) throws SQLException
	{
		return rs.getObject(index, LocalDate.class);
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields,
			Class<?> type) throws SQLException
	{
		return rs.getObject(fields, LocalDate.class);
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int index, Object value) throws SQLException
	{
		ps.setObject(index, value);
		return index + 1;
	}
}
