package gate.converter;

import gate.constraint.Constraint;
import gate.constraint.Pattern;
import gate.error.ConversionException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.DayOfWeek;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class MonthConverter implements Converter
{

	@Override
	public Object readFromResultSet(ResultSet rs, int fields, Class<?> type) throws SQLException, ConversionException
	{
		int value = rs.getInt(fields);
		return rs.wasNull() ? null : Month.of(value);
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException, ConversionException
	{
		int value = rs.getInt(fields);
		return rs.wasNull() ? null : Month.of(value);
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int fields, Object value) throws SQLException
	{
		if (value != null)
			ps.setInt(fields++, ((Month) value).getValue());
		else
			ps.setNull(fields++, Types.INTEGER);
		return fields;
	}

	@Override
	public String toText(Class<?> type, Object object)
	{
		return object != null ? ((Month) object).getDisplayName(TextStyle.FULL, Locale.getDefault()) : "";
	}

	@Override
	public String toText(Class<?> type, Object object, String format)
	{
		return object != null ? ((Month) object).getDisplayName(TextStyle.valueOf(format), Locale.getDefault()) : "";
	}

	@Override
	public String getMask()
	{
		return null;
	}

	@Override
	public String getDescription()
	{
		return "Mês do Ano";
	}

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		List<Constraint.Implementation<?>> constraints = new LinkedList<>();
		constraints.add(new Pattern.Implementation("^APRIL|AUGUST|DECEMBER|FEBRUARY|JANUARY|JULY|JUNE|MARCH|MAY|NOVEMBER|OCTOBER|SEPTEMBER$"));
		return constraints;
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? ((Month) object).name() : "";
	}

	@Override
	public Object ofString(Class<?> type, String string) throws ConversionException
	{
		if (string == null)
			return null;
		string = string.trim();
		if (string.isEmpty())
			return null;

		return Month.valueOf(string);
	}

}
