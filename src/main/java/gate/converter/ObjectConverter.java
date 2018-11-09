package gate.converter;

import gate.constraint.Constraint;
import gate.error.ConversionException;
import gate.io.Encoder;
import gate.lang.json.JsonScanner;
import gate.lang.json.JsonToken;
import gate.lang.json.JsonWriter;
import gate.util.Generics;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class ObjectConverter implements Converter
{

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return Collections.emptyList();
	}

	@Override
	public String getMask()
	{
		return null;
	}

	@Override
	public String getDescription()
	{
		return null;
	}

	@Override
	public Object ofString(Class<?> type, String string) throws ConversionException
	{
		if (string == null)
			return null;
		string = string.trim();
		if (string.isEmpty())
			return null;

		return Encoder.getInstance(type).decode(type, string);
	}

	@Override
	@SuppressWarnings("unchecked")
	public String toString(Class<?> type, Object object)
	{
		try
		{
			if (object == null)
				return "";

			return Encoder.getInstance(type).encode((Class<Object>) type, object);

		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	@Override
	public String toText(Class<?> type, Object object)
	{
		return object != null ? object.toString().trim() : "";
	}

	@Override
	public String toText(Class<?> type, Object object, String format)
	{
		return String.format(format, toText(type, object));
	}

	@Override
	public Object readFromResultSet(ResultSet rs, int fields, Class<?> type)
			throws SQLException, ConversionException,
			ConversionException
	{
		String value = rs.getString(fields);
		return rs.wasNull() ? null : ofString(type, value);
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException, ConversionException
	{
		String value = rs.getString(fields);
		return rs.wasNull() ? null : ofString(type, value);
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int fields, Object value) throws SQLException,
			ConversionException
	{
		if (value != null)
			ps.setString(fields++, toString(value.getClass(), value));
		else
			ps.setNull(fields++, Types.VARCHAR);
		return fields;
	}

	@Override
	public Object ofJson(JsonScanner scanner, Type type, Type elementType) throws ConversionException
	{
		try
		{
			if (scanner.getCurrent().getType() != JsonToken.Type.OPEN_OBJECT)
				throw new ConversionException(scanner.getCurrent() + " is not a valid JSON object");

			boolean empty = true;
			Class<?> clazz = (Class) type;
			Constructor<?> constructor
					= Stream.of(clazz.getDeclaredConstructors())
							.filter(e -> e.getParameterCount() == 0)
							.findAny().orElseThrow(() -> new ConversionException(
							"No default constructor found on " + clazz.getName()));
			constructor.setAccessible(true);
			Object object = constructor.newInstance();

			do
			{
				scanner.scan();
				if (scanner.getCurrent().getType() != JsonToken.Type.CLOSE_OBJECT)
				{
					empty = false;
					if (scanner.getCurrent().getType() != JsonToken.Type.STRING)
						throw new ConversionException(scanner.getCurrent() + " is not a valid JSON object key");

					Field field = Generics.findField(clazz, scanner.getCurrent().toString())
							.orElseThrow(() -> new NoSuchFieldException(scanner.getCurrent().toString()));

					scanner.scan();
					if (scanner.getCurrent().getType() != JsonToken.Type.DOUBLE_DOT)
						throw new ConversionException(scanner.getCurrent() + " is not a valid JSON object");

					scanner.scan();
					Type genericType = field.getGenericType();
					Converter converter = Converter.getConverter(field.getType());
					Object value = converter.ofJson(scanner, genericType, Generics.getElementType(
							genericType));
					field.set(object, value);
				} else if (!empty)
					throw new ConversionException("the specified JsonElement is not a JsonObject");
			} while (scanner.getCurrent().getType() == JsonToken.Type.COMMA);

			if (scanner.getCurrent().getType() != JsonToken.Type.CLOSE_OBJECT)
				throw new ConversionException(scanner.getCurrent() + " is not a valid JSON object");

			scanner.scan();
			return object;
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchFieldException ex)
		{
			throw new ConversionException(ex.getMessage());
		}
	}

	@Override
	public <T> void toJson(JsonWriter writer, Class<T> type, T object) throws ConversionException
	{
		try
		{
			writer.write(JsonToken.Type.OPEN_OBJECT, null);

			boolean first = true;
			for (Field field : Generics.getFields(Generics.getRawType(type)))
			{
				if (!Modifier.isTransient(field.getModifiers())
						&& !Modifier.isStatic(field.getModifiers()))
				{
					field.setAccessible(true);
					Object value = field.get(object);
					if (value != null)
					{
						if (first)
							first = false;
						else
							writer.write(JsonToken.Type.COMMA, null);

						writer.write(JsonToken.Type.STRING, field.getName());
						writer.write(JsonToken.Type.DOUBLE_DOT, null);
						Converter converter = Converter.getConverter(field.getType());
						converter.toJson(writer, (Class<Object>) field.getType(), value);
					}
				}
			}

			writer.write(JsonToken.Type.CLOSE_OBJECT, null);
		} catch (IllegalAccessException ex)
		{
			throw new ConversionException(ex.getMessage());
		}
	}

}
