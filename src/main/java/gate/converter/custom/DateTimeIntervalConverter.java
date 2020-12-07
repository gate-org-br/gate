package gate.converter.custom;

import gate.constraint.Constraint;
import gate.constraint.Maxlength;
import gate.constraint.Pattern;
import gate.converter.Converter;
import gate.error.ConversionException;
import gate.type.DateTime;
import gate.type.DateTimeInterval;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class DateTimeIntervalConverter implements Converter
{

	private static final List<String> SUFIXES
		= Arrays.asList("dateTime1", "dateTime2");

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
			return DateTimeInterval.of(string);
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
		return object != null ? DateTimeInterval.formatter(format).format((DateTimeInterval) object) : "";
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

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
		constraints.add(new Pattern.Implementation("^[0-9]{2}[/][0-9]{2}[/][0-9]{4} [0-9]{2}[:][0-9]{2} [-] [0-9]{2}[/][0-9]{2}[/][0-9]{4} [0-9]{2}[:][0-9]{2}$"));
		return constraints;
	}

	@Override
	public List<String> getSufixes()
	{
		return SUFIXES;
	}

	@Override
	public Object readFromResultSet(ResultSet rs, int fields, Class<?> type) throws SQLException
	{
		java.sql.Timestamp min = rs.getTimestamp(fields);
		if (rs.wasNull())
			return null;
		java.sql.Timestamp max = rs.getTimestamp(fields + 1);
		if (rs.wasNull())
			return null;
		return new DateTimeInterval(DateTime.of(min), DateTime.of(max));
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException
	{
		java.sql.Timestamp min = rs.getTimestamp(fields + Converter.SEPARATOR + SUFIXES.get(0));
		if (rs.wasNull())
			return null;
		java.sql.Timestamp max = rs.getTimestamp(fields + Converter.SEPARATOR + SUFIXES.get(1));
		if (rs.wasNull())
			return null;
		return new DateTimeInterval(DateTime.of(min), DateTime.of(max));
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int fields, Object value) throws SQLException
	{
		if (value != null)
		{
			ps.setTimestamp(fields++, new java.sql.Timestamp(((DateTimeInterval) value).getMin().getValue()));
			ps.setTimestamp(fields++, new java.sql.Timestamp(((DateTimeInterval) value).getMax().getValue()));
		} else
		{
			ps.setNull(fields++, Types.TIMESTAMP);
			ps.setNull(fields++, Types.TIMESTAMP);
		}
		return fields;
	}
}
