package gate.converter.custom;

import gate.constraint.Constraint;
import gate.constraint.Pattern;
import gate.converter.Converter;
import gate.error.ConversionException;
import gate.type.Period;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.LinkedList;
import java.util.List;

public class PeriodConverter implements Converter
{

	@Override
	public Number toNumber(Class<?> type, Object object)
	{
		if (object != null)
		{
			Period period = (Period) object;
			return new BigDecimal(period.getValue())
					.divide(new BigDecimal(60), 2, RoundingMode.HALF_EVEN);
		}

		return null;
	}

	@Override
	public Object ofString(Class<?> type, String string) throws ConversionException
	{
		if (string != null && string.trim().length() > 0)
		{
			try
			{
				return Period.of(string);
			} catch (IllegalArgumentException e)
			{
				throw new ConversionException(getDescription());
			}
		}

		return null;
	}

	@Override
	public String toText(Class<?> type, Object object)
	{
		return object != null ? ((Period) object).toString() : "";
	}

	@Override
	public String toText(Class<?> type, Object object, String format)
	{
		return object != null ? String.format(format, toText(type, object)) : "";
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? ((Period) object).toString() : "";
	}

	@Override
	public String getDescription()
	{
		return "Entre com um período no formato hh:mm:ss";
	}

	@Override
	public String getMask()
	{
		return null;
	}

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		List<Constraint.Implementation<?>> constraints = new LinkedList<>();
		constraints.add(new Pattern.Implementation("^[0-9][0-9][0-9]*:[0-9][0-9]:[0-9][0-9]$"));
		return constraints;
	}

	@Override
	public Object readFromResultSet(ResultSet rs, int fields, Class<?> type) throws SQLException
	{
		long value = rs.getLong(fields);
		return rs.wasNull() ? null : Period.of(value);
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException
	{
		long value = rs.getLong(fields);
		return rs.wasNull() ? null : Period.of(value);
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int fields, Object value) throws SQLException
	{
		if (value == null)
			ps.setNull(fields++, Types.INTEGER);
		else
			ps.setLong(fields++, ((Period) value).getValue());
		return fields;
	}
}
