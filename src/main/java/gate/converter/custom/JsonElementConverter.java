package gate.converter.custom;

import gate.constraint.Constraint;
import gate.converter.Converter;
import gate.error.ConversionException;
import gate.lang.json.JsonArray;
import gate.lang.json.JsonBoolean;
import gate.lang.json.JsonElement;
import gate.lang.json.JsonNull;
import gate.lang.json.JsonNumber;
import gate.lang.json.JsonObject;
import gate.lang.json.JsonScanner;
import gate.lang.json.JsonString;
import gate.lang.json.JsonToken;
import gate.lang.json.JsonWriter;
import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collections;
import java.util.List;

public class JsonElementConverter implements Converter
{

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
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return Collections.emptyList();
	}

	@Override
	public Object ofString(Class<?> type, String string) throws ConversionException
	{
		if (string != null)
		{
			string = string.trim();
			if (!string.isEmpty())
				return JsonElement.parse(string);
		}
		return null;
	}

	@Override
	public String toText(Class<?> type, Object object)
	{
		if (object != null)
			return JsonElement.format((JsonElement) object);
		return null;
	}

	@Override
	public String toText(Class<?> type, Object object, String format)
	{
		if (object != null)
			return String.format(format, JsonElement.format((JsonElement) object));
		return null;
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		if (object != null)
			return JsonElement.format((JsonElement) object);
		return null;
	}

	@Override
	public Object readFromResultSet(ResultSet rs, int fields, Class<?> type)
			throws SQLException, ConversionException
	{
		String value = rs.getString(fields);
		if (!rs.wasNull())
			return JsonElement.parse(value);
		return null;
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type)
			throws SQLException, ConversionException
	{
		String value = rs.getString(fields);
		if (!rs.wasNull())
			return JsonElement.parse(value);
		return null;
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int fields, Object value)
			throws SQLException
	{
		if (value != null)
			ps.setString(fields++, JsonElement.format((JsonElement) value));
		else
			ps.setNull(fields++, Types.VARCHAR);
		return fields;
	}

	@Override
	public <T> void toJson(JsonWriter writer, Class<T> type, T object) throws ConversionException
	{
		writer.write(object != null ? object.toString() : "null");
	}

	@Override
	public Object ofJson(JsonScanner scanner, Type type, Type elementType)
			throws ConversionException
	{
		switch (scanner.getCurrent().getType())
		{
			case NUMBER -> {
				JsonNumber value = JsonNumber.parse(scanner.getCurrent().toString());
				scanner.scan();
				return value;
			}
			case STRING -> {
				JsonString value = JsonString.parse(scanner.getCurrent().toString());
				scanner.scan();
				return value;
			}
			case FALSE -> {
				scanner.scan();
				return JsonBoolean.FALSE;
			}
			case TRUE -> {
				scanner.scan();
				return JsonBoolean.TRUE;
			}
			case NULL -> {
				scanner.scan();
				return JsonNull.INSTANCE;
			}
			case OPEN_ARRAY -> {
				boolean empty = true;

				JsonArray object = new JsonArray();

				do
				{
					scanner.scan();
					if (scanner.getCurrent().getType() != JsonToken.Type.CLOSE_ARRAY)
					{
						empty = false;

						scanner.scan();
						Converter converter = Converter.getConverter(JsonElement.class);
						JsonElement value =
								(JsonElement) converter.ofJson(scanner, elementType, elementType);
						object.add(value);
					} else if (!empty)
						throw new ConversionException(
								"Unexpected token on json input: " + scanner.getCurrent());
				} while (scanner.getCurrent().getType() == JsonToken.Type.COMMA);

				if (scanner.getCurrent().getType() != JsonToken.Type.CLOSE_ARRAY)
					throw new ConversionException(
							"Unexpected token on json input: " + scanner.getCurrent());

				scanner.scan();
				return object;
			}
			case OPEN_OBJECT -> {

				boolean empty = true;

				JsonObject object = new JsonObject();

				do
				{
					scanner.scan();
					if (scanner.getCurrent().getType() != JsonToken.Type.CLOSE_OBJECT)
					{
						empty = false;
						if (scanner.getCurrent().getType() != JsonToken.Type.STRING)
							throw new ConversionException(
									"Unexpected token on json input: " + scanner.getCurrent());

						String key = scanner.getCurrent().toString();

						scanner.scan();
						if (scanner.getCurrent().getType() != JsonToken.Type.DOUBLE_DOT)
							throw new ConversionException(
									"Unexpected token on json input: " + scanner.getCurrent());

						scanner.scan();
						Converter converter = Converter.getConverter(JsonElement.class);
						JsonElement value =
								(JsonElement) converter.ofJson(scanner, elementType, elementType);
						object.put(key, value);
					} else if (!empty)
						throw new ConversionException(
								"Unexpected token on json input: " + scanner.getCurrent());
				} while (scanner.getCurrent().getType() == JsonToken.Type.COMMA);

				if (scanner.getCurrent().getType() != JsonToken.Type.CLOSE_OBJECT)
					throw new ConversionException(
							"Unexpected token on json input: " + scanner.getCurrent());

				scanner.scan();
				return object;
			}
			default -> throw new ConversionException(
					"Unexpected token on json input: " + scanner.getCurrent());
		}

	}
}
