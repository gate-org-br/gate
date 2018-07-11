package gate.converter;

import gate.constraint.Constraint;
import gate.constraint.Pattern;
import gate.error.ConversionException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class DurationConverter implements Converter
{

	protected final static long S = 1;
	protected final static long M = 60 * S;
	protected final static long H = 60 * M;

	private static final List<Constraint.Implementation<?>> CONSTRAINTS
			= Arrays.asList(new Pattern.Implementation("^(\\d+:)?(\\d+:)?\\d+$"));

	@Override
	public String getMask()
	{
		return null;
	}

	@Override
	public String getDescription()
	{
		return "Campos de duração devem estar no formato HH:MM:SS";
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
		long value = rs.getLong(fields);
		if (rs.wasNull())
			return null;
		return Duration.ofSeconds(value);
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int index, Object value) throws SQLException,
			ConversionException
	{
		if (value == null)
			ps.setNull(index++, Types.INTEGER);
		else
			ps.setLong(index++, ((Duration) value).getSeconds());
		return index;
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		if (object == null)
			return "";
		Duration duration = (Duration) object;
		long value = duration.getSeconds();
		long h = value / H;
		long m = (value % H) / M;
		long s = ((value % H) % M) / S;
		return String.format("%02d:%02d:%02d", h, m, s);
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

	@Override
	public Object ofString(Class<?> type, String string) throws ConversionException
	{
		if (string == null)
			return null;

		string = string.trim();
		if (string.isEmpty())
			return null;

		int i = 0;
		long value = 0;
		long field = -1;

		while (i < string.length())
			switch (string.charAt(i++))
			{
				case '0':
					field = field == -1 ? 0 : (field * 10) + 0;
					break;
				case '1':
					field = field == -1 ? 1 : (field * 10) + 1;
					break;
				case '2':
					field = field == -1 ? 2 : (field * 10) + 2;
					break;
				case '3':
					field = field == -1 ? 3 : (field * 10) + 3;
					break;
				case '4':
					field = field == -1 ? 4 : (field * 10) + 4;
					break;
				case '5':
					field = field == -1 ? 5 : (field * 10) + 5;
					break;
				case '6':
					field = field == -1 ? 6 : (field * 10) + 6;
					break;
				case '7':
					field = field == -1 ? 7 : (field * 10) + 7;
					break;
				case '8':
					field = field == -1 ? 8 : (field * 10) + 8;
					break;
				case '9':
					field = field == -1 ? 9 : (field * 10) + 9;
					break;
				case ':':
					if (field == -1)
						throw new ConversionException(string + " não é uma duração válida");
					value = value * 60 + field * 60;
					field = -1;
					break;
				default:
					throw new ConversionException(string + " não é uma duração válida");
			}

		if (field == -1)
			throw new ConversionException(string + " não é uma duração válida");
		return Duration.ofSeconds(value + field);
	}
}
