package gate.converter.custom;

import gate.constraint.Constraint;
import gate.constraint.Maxlength;
import gate.constraint.Pattern;
import gate.converter.Converter;
import gate.error.ConversionException;
import gate.type.LocalDateTimeInterval;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class LocalDateTimeIntervalConverter implements Converter
{

	private static final List<String> SUFIXES = Arrays.asList("min", "max");

	@Override
	public String getDescription()
	{
		return "Campos de intervalo de data/hora devem ser preenchidos no formato DD/MM/YYYY HH:MM - DD/MM/YYYY HH:MM";
	}

	@Override
	public String getMask()
	{
		return "##/##/#### ##:## - ##/##/#### ##:##";
	}

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		List<Constraint.Implementation<?>> constraints = new LinkedList<>();
		constraints.add(new Maxlength.Implementation(35));
		constraints.add(new Pattern.Implementation("^[0-9]{2}/[0-9]{2}/[0-9]{4} [0-9]{2}:[0-9]{2} - [0-9]{2}/[0-9]{2}/[0-9]{4} [0-9]{2}:[0-9]{2}$"));
		return constraints;
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
			return LocalDateTimeInterval.of(string);
		} catch (ParseException ex)
		{
			throw new ConversionException(ex, getDescription());
		}
	}

	@Override
	public String toText(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

	@Override
	public String toText(Class<?> type, Object object, String format)
	{
		return object != null ? LocalDateTimeInterval.formatter(format).format((LocalDateTimeInterval) object) : "";
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? ((LocalDateTimeInterval) object).toString() : "";
	}

	@Override
	public List<String> getSufixes()
	{
		return SUFIXES;
	}

	@Override
	public Object readFromResultSet(ResultSet rs, int fields, Class<?> type) throws SQLException
	{
		LocalDateTime min = rs.getObject(fields, LocalDateTime.class);
		if (rs.wasNull())
			return null;
		LocalDateTime max = rs.getObject(fields + 1, LocalDateTime.class);
		if (rs.wasNull())
			return null;
		return LocalDateTimeInterval.of(min, max);
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException
	{
		LocalDateTime min = rs.getObject(fields + Converter.SEPARATOR + SUFIXES.get(0), LocalDateTime.class);
		if (rs.wasNull())
			return null;
		LocalDateTime max = rs.getObject(fields + Converter.SEPARATOR + SUFIXES.get(1), LocalDateTime.class);
		if (rs.wasNull())
			return null;
		return LocalDateTimeInterval.of(min, max);
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int fields, Object value) throws SQLException
	{
		if (value != null)
		{
			ps.setObject(fields++, ((LocalDateTimeInterval) value).getMin());
			ps.setObject(fields++, ((LocalDateTimeInterval) value).getMax());
		} else
		{
			ps.setNull(fields++, Types.TIMESTAMP);
			ps.setNull(fields++, Types.TIMESTAMP);
		}
		return fields;
	}
}
