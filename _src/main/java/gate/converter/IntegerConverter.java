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
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class IntegerConverter implements Converter
{

	private static final NumberFormat FORMAT = NumberFormat.getInstance(Locale.getDefault());

	static
	{
		FORMAT.setGroupingUsed(true);
	}

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
		return (Number) object;
	}

	@Override
	public String toText(Class<?> type, Object object)
	{
		return object != null ? FORMAT.format(object) : "";
	}

	@Override
	public String toText(Class<?> type, Object object, String format)
	{
		return object != null ? String.format(format, object) : "";
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
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

			Number number = FORMAT.parse(string);
			if (number instanceof Integer)
				return number;

			return number.intValue();
		} catch (ParseException ex)
		{
			throw new ConversionException(ex, "%s não é um inteiro válido.", string);
		}
	}

	@Override
	public Object readFromResultSet(ResultSet rs, int fields, Class<?> type) throws SQLException
	{
		int value = rs.getInt(fields);
		return rs.wasNull() ? null : value;
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException
	{
		int value = rs.getInt(fields);
		return rs.wasNull() ? null : value;
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int fields, Object value) throws SQLException
	{
		if (value != null)
			ps.setInt(fields++, (Integer) value);
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
				Integer value = Integer.valueOf(scanner.getCurrent().toString());
				scanner.scan();
				return value;
			default:
				throw new ConversionException(scanner.getCurrent() + " is not a number");
		}
	}

	@Override
	public <T> void toJson(JsonWriter writer, Class<T> type, T object) throws ConversionException
	{
		if (object == null)
			writer.write(JsonToken.Type.NULL, null);
		else if (object instanceof Integer)
			writer.write(JsonToken.Type.NUMBER, object.toString());
		else
			throw new ConversionException(object.getClass().getName() + " is not an Integer");
	}
}
