package gate.converter.custom;

import gate.constraint.Constraint;
import gate.constraint.Maxlength;
import gate.constraint.Pattern;
import gate.converter.Converter;
import gate.error.ConversionException;
import gate.type.Time;
import gate.type.TimeInterval;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class TimeIntervalConverter implements Converter
{

	private static final List<String> SUFIXES
		= Arrays.asList("time1", "time2");

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
			return TimeInterval.of(string);
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
		return object != null ? ((TimeInterval) object).format(format) : "";
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

	@Override
	public String getDescription()
	{
		return "Campos de intervalo de hora devem ser preenchidos no formato HH:MM - HH:MM";
	}

	@Override
	public String getMask()
	{
		return "##:## - ##:##";
	}

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		List<Constraint.Implementation<?>> constraints = new LinkedList<>();
		constraints.add(new Maxlength.Implementation(13));
		constraints.add(new Pattern.Implementation("^[0-9]{2}[:][0-9]{2}[ ][-][ ][0-9]{2}[:][0-9]{2}$"));
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
		java.sql.Time value1 = rs.getTime(fields++);
		if (rs.wasNull())
			return null;
		java.sql.Time value2 = rs.getTime(fields++);
		if (rs.wasNull())
			return null;
		return new TimeInterval(Time.of(value1), Time.of(value2));
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException
	{
		java.sql.Time value1 = rs.getTime(fields + ':' + SUFIXES.get(0));
		if (rs.wasNull())
			return null;
		java.sql.Time value2 = rs.getTime(fields + ':' + SUFIXES.get(1));
		if (rs.wasNull())
			return null;
		return new TimeInterval(Time.of(value1), Time.of(value2));
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int fields, Object value) throws SQLException
	{
		if (value != null)
		{
			ps.setTime(fields++, new java.sql.Time(((TimeInterval) value).getMin().getValue()));
			ps.setTime(fields++, new java.sql.Time(((TimeInterval) value).getMax().getValue()));
		} else
		{
			ps.setNull(fields++, Types.TIME);
			ps.setNull(fields++, Types.TIME);
		}
		return fields;
	}
}
