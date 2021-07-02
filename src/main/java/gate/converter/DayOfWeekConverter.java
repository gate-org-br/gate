package gate.converter;

import gate.constraint.Constraint;
import gate.constraint.Pattern;
import gate.error.ConversionException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class DayOfWeekConverter implements Converter
{

	@Override
	public Object readFromResultSet(ResultSet rs, int fields, Class<?> type) throws SQLException, ConversionException
	{
		int value = rs.getInt(fields);
		return rs.wasNull() ? null : DayOfWeek.of(value - 1);
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException, ConversionException
	{
		int value = rs.getInt(fields);
		return rs.wasNull() ? null : DayOfWeek.of(value - 1);
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int fields, Object value) throws SQLException
	{
		if (value != null)
			ps.setInt(fields++, ((DayOfWeek) value).getValue() + 1);
		else
			ps.setNull(fields++, Types.INTEGER);
		return fields;
	}

	@Override
	public String toText(Class<?> type, Object object)
	{
		return object != null ? ((DayOfWeek) object).getDisplayName(TextStyle.FULL, Locale.getDefault()) : "";
	}

	@Override
	public String toText(Class<?> type, Object object, String format)
	{
		return object != null ? ((DayOfWeek) object).getDisplayName(TextStyle.valueOf(format), Locale.getDefault()) : "";
	}

	@Override
	public String getMask()
	{
		return null;
	}

	@Override
	public String getDescription()
	{
		return "Dia da Semana";
	}

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		List<Constraint.Implementation<?>> constraints = new LinkedList<>();
		constraints.add(new Pattern.Implementation("^FRIDAY|MONDAY|SATURDAY|SUNDAY|THURSDAY|TUESDAY|WEDNESDAY$"));
		return constraints;
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? ((DayOfWeek) object).name() : "";
	}

	@Override
	public Object ofString(Class<?> type, String string) throws ConversionException
	{
		if (string == null)
			return null;
		string = string.trim();
		if (string.isEmpty())
			return null;

		return DayOfWeek.valueOf(string);
	}

}
