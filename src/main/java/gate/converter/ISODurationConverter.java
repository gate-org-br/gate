package gate.converter;

import gate.constraint.Constraint;
import gate.error.ConversionException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.Duration;
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ISODurationConverter implements Converter
{

	private static final Pattern PATTERN
		= Pattern.compile("^ *(([0-9]+)[dD])? *(([0-9]+)[hH])? *(([0-9]+)[mM])? *(([0-9]+)[sS])?|([0-9]+) *$");

	private static final Pattern ALTERNATIVE
		= Pattern.compile("^ *([0-9]{2}):([0-9]{2})(:([0-9]{2}))? *$");

	private static final List<Constraint.Implementation<?>> CONSTRAINTS = List.of(new gate.constraint.Pattern.Implementation(PATTERN.toString()));

	@Override
	public String getMask()
	{
		return null;
	}

	@Override
	public String getDescription()
	{
		return "Duração no formato dD hH mM sS onde d, h, m e s são o número de dias, horas, minutos e segundos respectivamente";
	}

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return CONSTRAINTS;
	}

	@Override
	public Object readFromResultSet(ResultSet rs, int index, Class<?> type) throws SQLException, ConversionException
	{
		long value = rs.getLong(index);
		if (rs.wasNull())
			return null;
		return Duration.ofSeconds(value);
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException, ConversionException
	{
		int value = rs.getInt(fields);
		if (rs.wasNull())
			return null;
		return Duration.ofSeconds(value);
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int index, Object value) throws SQLException
	{
		if (value != null)
			ps.setLong(index, ((Duration) value).toSeconds());
		else
			ps.setNull(index, Types.INTEGER);
		return index + 1;
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		if (object == null)
			return "";
		Duration duration = (Duration) object;
		StringJoiner string = new StringJoiner(" ");
		if (duration.toDaysPart() > 0)
			string.add(duration.toDaysPart() + "d");
		if (duration.toHoursPart() > 0)
			string.add(duration.toHoursPart() + "h");
		if (duration.toMinutesPart() > 0)
			string.add(duration.toMinutesPart() + "m");
		if (duration.toSecondsPart() > 0)
			string.add(duration.toSecondsPart() + "s");
		return string.toString();
	}

	@Override
	public Object ofString(Class<?> type, String string) throws ConversionException
	{
		Matcher matcher = PATTERN.matcher(string);
		if (matcher.matches())
		{
			if (matcher.group(9) != null)
				return Duration.ofMinutes(Integer.parseInt(matcher.group(9)));
			int d = matcher.group(2) != null ? Integer.parseInt(matcher.group(2)) : 0;
			int h = matcher.group(4) != null ? Integer.parseInt(matcher.group(4)) : 0;
			int m = matcher.group(6) != null ? Integer.parseInt(matcher.group(6)) : 0;
			int s = matcher.group(8) != null ? Integer.parseInt(matcher.group(8)) : 0;

			return Duration.ofSeconds(d * 24 * 60 * 60 + h * 60 * 60 + m * 60 + s);
		}

		matcher = ALTERNATIVE.matcher(string);
		if (!matcher.matches())
			throw new ConversionException(string + " não é uma duração válida");

		int h = matcher.group(1) != null ? Integer.parseInt(matcher.group(1)) : 0;
		int m = matcher.group(2) != null ? Integer.parseInt(matcher.group(2)) : 0;
		int s = matcher.group(4) != null ? Integer.parseInt(matcher.group(4)) : 0;
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
		return String.format(format, toString(type, object));
	}
}
