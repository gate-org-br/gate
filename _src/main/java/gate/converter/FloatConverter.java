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

public class FloatConverter implements Converter
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
		return (Number) object;
	}

	@Override
	public Object ofString(Class<?> type, String string) throws ConversionException
	{
		try
		{
			return string != null && string.trim().length() > 0 ? Float.valueOf(getFormat().parse(string).floatValue()) : null;
		} catch (ParseException e)
		{
			throw new ConversionException(String.format("%s não é um decimal válido.", string));
		}
	}

	@Override
	public String toText(Class<?> type, Object object)
	{
		return object != null ? getFormat().format(object) : "";
	}

	@Override
	public String toText(Class<?> type, Object object, String format)
	{
		return object != null ? String.format(format, object) : "";
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? getFormat().format(object) : "";
	}

	private NumberFormat getFormat()
	{
		NumberFormat format = NumberFormat.getInstance(Locale.getDefault());
		format.setMinimumFractionDigits(1);
		return format;
	}

	@Override
	public Object readFromResultSet(ResultSet rs, int fields, Class<?> type) throws SQLException
	{
		float value = rs.getFloat(fields);
		return rs.wasNull() ? null : value;
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException
	{
		float value = rs.getFloat(fields);
		return rs.wasNull() ? null : value;
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int fields, Object value) throws SQLException
	{
		if (value != null)
			ps.setFloat(fields++, ((Float) value));
		else
			ps.setNull(fields++, Types.FLOAT);
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
				Float value = Float.valueOf(scanner.getCurrent().toString());
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
		else if (object instanceof Float)
			writer.write(JsonToken.Type.NUMBER, object.toString());
		else
			throw new ConversionException(object.getClass().getName() + " is not a Float");
	}
}
