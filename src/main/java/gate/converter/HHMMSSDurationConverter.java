package gate.converter;

import gate.constraint.Constraint;
import gate.constraint.Maxlength;
import gate.error.ConversionException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.Duration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HHMMSSDurationConverter implements Converter
{

	private static final Pattern PATTERN = Pattern.compile("^ *([0-9][0-9]):([0-5][0-9]):([0-5][0-9]) *$");

	private static final List<Constraint.Implementation<?>> CONSTRAINTS
		= List.of(new gate.constraint.Pattern.Implementation(PATTERN.toString()), new Maxlength.Implementation(8));

	@Override
	public String getMask()
	{
		return "##:##:##";
	}

	@Override
	public String getDescription()
	{
		return "Duração no formato HH:MM:SS";
	}

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return CONSTRAINTS;
	}

	@Override
	public Object readFromResultSet(ResultSet rs, int fields, Class<?> type) throws SQLException
	{
		long value = rs.getLong(fields);
		return rs.wasNull() ? null : Duration.ofSeconds(value);
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException
	{
		long value = rs.getLong(fields);
		return rs.wasNull() ? null : Duration.ofSeconds(value);
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int index, Object value) throws SQLException
	{
		if (value != null)
			ps.setLong(index++, ((Duration) value).getSeconds());
		else
			ps.setNull(index++, Types.INTEGER);
		return index;
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		if (object == null)
			return "";
		Duration duration = (Duration) object;
		return String.format("%02d:%02d:%02d", duration.toHoursPart(), duration.toMinutesPart(), duration.toSecondsPart());
	}

	@Override
	public Object ofString(Class<?> type, String string) throws ConversionException
	{
		if (string.isBlank())
			return null;

		Matcher matcher = PATTERN.matcher(string);
		if (!matcher.matches())
			throw new ConversionException(string + " não é uma duração válida");

		int h = matcher.group(1) != null ? Integer.parseInt(matcher.group(1)) : 0;
		int m = matcher.group(2) != null ? Integer.parseInt(matcher.group(2)) : 0;
		int s = matcher.group(3) != null ? Integer.parseInt(matcher.group(3)) : 0;
		return Duration.ofSeconds(h * 60 * 60 + m * 60 + s);
	}

	@Override
	public String toText(Class<?> type, Object object)
	{
		return toString(type, object);
	}

	@Override
	public String toText(Class<?> type, Object object, String format)
	{
		return toString(type, object);
	}
}
