package gate.converter.custom;

import gate.constraint.Constraint;
import gate.error.ConversionException;
import gate.constraint.Maxlength;
import gate.constraint.Pattern;
import gate.converter.Converter;
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
		if (string != null)
		{
			string = string.trim();
			if (!string.isEmpty())
			{
				try
				{
					return new DateTimeInterval(string);
				} catch (ParseException e)
				{
					throw new ConversionException(String.format(getDescription()));
				}
			}
		}

		return null;
	}

	@Override
	public String toText(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

	@Override
	public String toText(Class<?> type, Object object, String format)
	{
		return object != null ? ((DateTimeInterval) object).format(format) : "";
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
	public Object readFromResultSet(ResultSet rs, int fields, Class<?> type) throws SQLException, ConversionException
	{
		java.sql.Timestamp value1 = rs.getTimestamp(fields);
		if (rs.wasNull())
			return null;
		java.sql.Timestamp value2 = rs.getTimestamp(fields + 1);
		if (rs.wasNull())
			return null;
		return new DateTimeInterval(new DateTime(value1), new DateTime(value2));
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException
	{
		java.sql.Timestamp value1 = rs.getTimestamp(fields + ":" + SUFIXES.get(0));
		if (rs.wasNull())
			return null;
		java.sql.Timestamp value2 = rs.getTimestamp(fields + ":" + SUFIXES.get(1));
		if (rs.wasNull())
			return null;
		return new DateTimeInterval(new DateTime(value1), new DateTime(value2));
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int fields, Object value) throws SQLException
	{
		if (value != null)
		{
			ps.setTimestamp(fields++, new java.sql.Timestamp(((DateTimeInterval) value).getDateTime1().getValue()));
			ps.setTimestamp(fields++, new java.sql.Timestamp(((DateTimeInterval) value).getDateTime2().getValue()));
		} else
		{
			ps.setNull(fields++, Types.DATE);
			ps.setNull(fields++, Types.DATE);
		}
		return fields;
	}
}
