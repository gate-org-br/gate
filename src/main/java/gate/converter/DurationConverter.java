package gate.converter;

import gate.constraint.Constraint;
import gate.constraint.Pattern;
import gate.error.ConversionException;
import gate.lang.json.JsonScanner;
import gate.lang.json.JsonToken;
import gate.lang.json.JsonWriter;
import gate.util.DurationFormatter;
import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;

public class DurationConverter implements Converter
{

	private static final List<Constraint.Implementation<?>> CONSTRAINTS
		= Collections.singletonList(new Pattern.Implementation(DurationFormatter.PATTERN.toString()));

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return CONSTRAINTS;
	}

	@Override
	public String getDescription()
	{
		return "Campos de duração devem estar no formato HH:MM:SS";
	}

	@Override
	public String getMask()
	{
		return null;
	}

	@Override
	public Object ofString(Class<?> type, String string) throws ConversionException
	{
		try
		{
			if (string == null)
				return null;
			string = string.trim();
			if (string.isEmpty())
				return null;

			return DurationFormatter.parse(string);
		} catch (DateTimeParseException ex)
		{
			throw new ConversionException(ex, "%s não representa uma duração no formato HH:MM:SS", string);
		}

	}

	@Override
	public String toText(Class<?> type, Object object)
	{
		return object != null ? DurationFormatter.format((Duration) object) : "";
	}

	@Override
	public String toText(Class<?> type, Object object, String format)
	{
		return object != null ? String.format(format, object) : "";
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? DurationFormatter.format((Duration) object) : "";
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
	public int writeToPreparedStatement(PreparedStatement ps, int fields, Object value) throws SQLException
	{
		if (value != null)
			ps.setLong(fields++, ((Duration) value).getSeconds());
		else
			ps.setNull(fields++, Types.INTEGER);
		return fields;
	}

	@Override
	public Object ofJson(JsonScanner scanner, Type type, Type elementType) throws ConversionException
	{
		switch (scanner.getCurrent().getType())
		{
			case NULL:
				scanner.scan();
				return null;
			case NUMBER:
				Duration value = Duration.ofSeconds(Long.parseLong(scanner.getCurrent().toString()));
				scanner.scan();
				return value;
			default:
				throw new ConversionException(scanner.getCurrent() + " is not a duration");
		}
	}

	@Override
	public <T> void toJson(JsonWriter writer, Class<T> type, T object) throws ConversionException
	{
		if (object == null)
			writer.write(JsonToken.Type.NULL, null);
		else if (object instanceof Duration)
			writer.write(JsonToken.Type.NUMBER, Long.toString(((Duration) object).getSeconds()));
		else
			throw new ConversionException(object.getClass().getName() + " is not a duration");
	}
}
