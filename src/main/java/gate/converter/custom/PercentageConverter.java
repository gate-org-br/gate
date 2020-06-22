package gate.converter.custom;

import gate.constraint.Constraint;
import gate.error.ConversionException;
import gate.converter.Converter;
import gate.type.Percentage;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class PercentageConverter implements Converter
{

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return Collections.emptyList();
	}

	@Override
	public String getDescription()
	{
		return null;
	}

	@Override
	public String getMask()
	{
		return null;
	}

	@Override
	public Number toNumber(Class<?> type, Object object)
	{
		return object != null ? ((Percentage) object).getValue() : null;
	}

	@Override
	public Object ofString(Class<?> type, String string) throws ConversionException
	{
		try
		{
			if (string == null || string.trim().length() == 0)
				return null;
			return new Percentage(string);
		} catch (ParseException e)
		{
			throw new ConversionException(String.format("%s não representa uma quantia válida.", string));
		}
	}

	@Override
	public String toText(Class<?> type, Object object)
	{
		if (object != null)
		{
			DecimalFormat format = new DecimalFormat("0.00");
			format.setCurrency(Currency.getInstance(Locale.getDefault()));
			format.setParseBigDecimal(true);
			return format.format(((Percentage) object).getValue());
		}
		return "";
	}

	@Override
	public String toText(Class<?> type, Object object, String format)
	{
		return object != null ? String.format(format, toText(type, object)) : "";
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? object.toString() : null;
	}

	@Override
	public Object readFromResultSet(ResultSet rs, int fields, Class<?> type) throws SQLException
	{
		BigDecimal value = rs.getBigDecimal(fields);
		return rs.wasNull() ? null : new Percentage(value);
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException
	{
		BigDecimal value = rs.getBigDecimal(fields);
		return rs.wasNull() ? null : new Percentage(value);
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int fields, Object value) throws SQLException
	{
		if (value != null)
			ps.setBigDecimal(fields++, ((Percentage) value).getValue());
		else
			ps.setNull(fields++, Types.DECIMAL);
		return fields;
	}
}
