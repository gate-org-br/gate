package gate.converter.custom;

import gate.constraint.Constraint;
import gate.constraint.Maxlength;
import gate.constraint.Pattern;
import gate.converter.Converter;
import gate.error.ConversionException;
import gate.type.Month;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

public class MonthConverter implements Converter
{

	private static final List<Constraint.Implementation<?>> CONSTRAINTS
		= Arrays.asList(new Maxlength.Implementation(7),
			new Pattern.Implementation("^[0-9]{2}/[0-9]{4}$"));

	@Override
	public String getDescription()
	{
		return "Campos de mês devem ser preenchidos no formato MM/YYYY";
	}

	@Override
	public String getMask()
	{
		return "##/####";
	}

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return CONSTRAINTS;
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
			return Month.of(string);
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
		return object != null ? Month.formatter(format).format((Month) object) : "";
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

	@Override
	public Object readFromResultSet(ResultSet rs, int fields, Class<?> type) throws SQLException
	{
		java.sql.Date value = rs.getDate(fields);
		return rs.wasNull() ? null : Month.of(value);
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException
	{
		java.sql.Date value = rs.getDate(fields);
		return rs.wasNull() ? null : Month.of(value);
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int fields, Object value) throws SQLException
	{
		if (value != null)
			ps.setDate(fields++, new java.sql.Date(((Month) value).getValue()));
		else
			ps.setNull(fields++, Types.DATE);
		return fields;
	}
}
