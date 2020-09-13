package gate.converter;

import gate.constraint.Constraint;
import gate.error.ConversionException;
import gate.lang.json.JsonScanner;
import gate.lang.json.JsonToken;
import gate.lang.json.JsonWriter;
import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collections;
import java.util.List;

public class BooleanConverter implements Converter
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
	public Object ofString(Class<?> type, String string)
	{
		return string != null && string.trim().length() > 0 ? Boolean.valueOf(string) : null;
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

	@Override
	public String toText(Class<?> type, Object object)
	{
		return object != null ? Boolean.TRUE.equals(object) ? "Sim" : "Não" : "";
	}

	@Override
	public String toText(Class<?> type, Object object, String format)
	{
		return object != null ? String.format(format, Boolean.TRUE.equals(object) ? "Sim" : "Não") : "";
	}

	@Override
	public Object readFromResultSet(ResultSet rs, int fields, Class<?> type) throws SQLException
	{
		boolean value = rs.getBoolean(fields);
		return rs.wasNull() ? null : value;
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException
	{
		boolean value = rs.getBoolean(fields);
		return rs.wasNull() ? null : value;
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int fields, Object value) throws SQLException
	{
		if (value != null)
			ps.setBoolean(fields++, (Boolean) value);
		else
			ps.setNull(fields++, Types.BOOLEAN);
		return fields;
	}

	@Override
	public Object ofJson(JsonScanner scanner, Type type, Type elementType) throws ConversionException
	{
		switch (scanner.getCurrent().getType())
		{
			case TRUE:
				scanner.scan();
				return Boolean.TRUE;
			case FALSE:
				scanner.scan();
				return Boolean.FALSE;
			case NULL:
				scanner.scan();
				return null;
			default:
				throw new ConversionException(scanner.getCurrent() + " is not a boolean");
		}
	}

	/**
	 * Serializes the specified {@link java.lang.Boolean} on JSON notation.
	 * <p>
	 * A non null java Boolean will be formatted as their respective true or
	 * false JSON boolean. A null reference will be formatted as a JSON
	 * Null.
	 *
	 * @throws gate.error.ConversionException if the specified object is not
	 * a boolean
	 */
	@Override
	public <T> void toJson(JsonWriter writer, Class<T> type, T object) throws ConversionException
	{
		if (object == null)
			writer.write(JsonToken.Type.NULL, null);
		else if (Boolean.TRUE.equals(object))
			writer.write(JsonToken.Type.TRUE, null);
		else if (Boolean.FALSE.equals(object))
			writer.write(JsonToken.Type.FALSE, null);
		else
			throw new ConversionException(object.getClass().getName() + " is not a boolean");
	}
}
