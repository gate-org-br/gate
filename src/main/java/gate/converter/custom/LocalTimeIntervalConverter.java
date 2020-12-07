package gate.converter.custom;

import gate.constraint.Constraint;
import gate.constraint.Maxlength;
import gate.constraint.Pattern;
import gate.converter.Converter;
import gate.error.ConversionException;
import gate.type.LocalTimeInterval;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class LocalTimeIntervalConverter implements Converter
{

	private static final List<String> SUFIXES = Arrays.asList("min", "max");

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
		constraints.add(new Pattern.Implementation("^[0-9]{2}:[0-9]{2} - [0-9]{2}:[0-9]{2}$"));
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
			return LocalTimeInterval.of(string);
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
		return object != null ? LocalTimeInterval.formatter(format).format((LocalTimeInterval) object) : "";
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? ((LocalTimeInterval) object).toString() : "";
	}

	@Override
	public List<String> getSufixes()
	{
		return SUFIXES;
	}

	@Override
	public Object readFromResultSet(ResultSet rs, int fields, Class<?> type) throws SQLException
	{
		LocalTime min = rs.getObject(fields, LocalTime.class);
		if (rs.wasNull())
			return null;
		LocalTime max = rs.getObject(fields + 1, LocalTime.class);
		if (rs.wasNull())
			return null;
		return LocalTimeInterval.of(min, max);
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException
	{
		LocalTime min = rs.getObject(fields + Converter.SEPARATOR + SUFIXES.get(0), LocalTime.class);
		if (rs.wasNull())
			return null;
		LocalTime max = rs.getObject(fields + Converter.SEPARATOR + SUFIXES.get(1), LocalTime.class);
		if (rs.wasNull())
			return null;
		return LocalTimeInterval.of(min, max);
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int fields, Object value) throws SQLException
	{
		if (value != null)
		{
			ps.setObject(fields++, ((LocalTimeInterval) value).getMin());
			ps.setObject(fields++, ((LocalTimeInterval) value).getMax());
		} else
		{
			ps.setNull(fields++, Types.TIME);
			ps.setNull(fields++, Types.TIME);
		}
		return fields;
	}
}
