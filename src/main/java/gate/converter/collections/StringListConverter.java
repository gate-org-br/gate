package gate.converter.collections;

import gate.constraint.Constraint;
import gate.converter.CollectionConverter;
import gate.converter.Converter;
import gate.error.ConversionException;
import gate.lang.json.JsonArray;
import gate.lang.json.JsonScanner;
import gate.lang.json.JsonToken;
import gate.type.collections.StringList;
import gate.util.Reflection;
import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class StringListConverter extends CollectionConverter
{

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return Collections.emptyList();
	}

	@Override
	public String getDescription()
	{
		return "Lista separada por vírgulas";
	}

	@Override
	public String getMask()
	{
		return null;
	}

	@Override
	public String toText(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

	@Override
	public String toText(Class<?> type, Object object, String format)
	{
		return object != null ? String.format(format, object.toString()) : "";
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

	@Override
	public Object ofString(Class<?> type, String string)
	{
		if (string == null)
			return null;
		return new StringList(string);
	}

	@Override
	public Object ofJson(JsonScanner scanner, Type type, Type elementType) throws ConversionException
	{
		if (scanner.getCurrent().getType() != JsonToken.Type.OPEN_ARRAY)
			throw new ConversionException(scanner.getCurrent() + " is not a collection");

		StringList value = new StringList();
		Converter converter = Converter.getConverter(String.class);

		do
		{
			scanner.scan();
			if (scanner.getCurrent().getType() != JsonToken.Type.CLOSE_ARRAY)
				value.add((String) converter.ofJson(scanner, elementType,
					Reflection.getElementType(elementType)));
			else if (!value.isEmpty())
				throw new ConversionException(scanner.getCurrent() + " is not a collection");
		} while (JsonToken.Type.COMMA == scanner.getCurrent().getType());

		if (scanner.getCurrent().getType() != JsonToken.Type.CLOSE_ARRAY)
			throw new ConversionException(scanner.getCurrent() + " is not a collection");

		scanner.scan();
		return value;
	}

	@Override
	public Object readFromResultSet(ResultSet rs, int fields, Class<?> type) throws SQLException, ConversionException
	{
		String value = rs.getString(fields);
		if (rs.wasNull())
			return null;
		return JsonArray.parse(value).stream().map(e -> e.toString())
			.collect(Collectors.toCollection(StringList::new));
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException, ConversionException
	{
		String value = rs.getString(fields);
		if (rs.wasNull())
			return null;
		return JsonArray.parse(value).stream().map(e -> e.toString())
			.collect(Collectors.toCollection(StringList::new));
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int fields, Object value) throws SQLException
	{
		if (value != null)
			ps.setString(fields++, JsonArray.format(value).toString());
		else
			ps.setNull(fields++, Types.VARCHAR);
		return fields;
	}

	public static class CommaConverter extends StringListConverter
	{

		@Override
		public String getDescription()
		{
			return "Lista separada por vírgulas";
		}

		@Override
		public Object ofString(Class<?> type, String string)
		{
			if (string == null)
				return null;
			return new StringList.Comma(string);
		}

		@Override
		public Object ofJson(JsonScanner scanner, Type type, Type elementType) throws ConversionException
		{
			if (scanner.getCurrent().getType() != JsonToken.Type.OPEN_ARRAY)
				throw new ConversionException(scanner.getCurrent() + " is not a collection");

			StringList.Comma value = new StringList.Comma();
			Converter converter = Converter.getConverter(String.class);

			do
			{
				scanner.scan();
				if (scanner.getCurrent().getType() != JsonToken.Type.CLOSE_ARRAY)
					value.add((String) converter.ofJson(scanner, elementType,
						Reflection.getElementType(elementType)));
				else if (!value.isEmpty())
					throw new ConversionException(scanner.getCurrent() + " is not a collection");
			} while (JsonToken.Type.COMMA == scanner.getCurrent().getType());

			if (scanner.getCurrent().getType() != JsonToken.Type.CLOSE_ARRAY)
				throw new ConversionException(scanner.getCurrent() + " is not a collection");

			scanner.scan();
			return value;

		}

		@Override
		public Object readFromResultSet(ResultSet rs, int fields, Class<?> type) throws SQLException, ConversionException
		{
			String value = rs.getString(fields);
			if (rs.wasNull())
				return null;
			return JsonArray.parse(value).stream().map(e -> e.toString())
				.collect(Collectors.toCollection(StringList.Comma::new));
		}

		@Override
		public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException, ConversionException
		{
			String value = rs.getString(fields);
			if (rs.wasNull())
				return null;
			return JsonArray.parse(value).stream().map(e -> e.toString())
				.collect(Collectors.toCollection(StringList.Comma::new));
		}
	}

	public static class SemicolonConverter extends StringListConverter
	{

		@Override
		public String getDescription()
		{
			return "Lista separada por ponto e vírgula";
		}

		@Override
		public Object ofString(Class<?> type, String string)
		{
			if (string == null)
				return null;
			return new StringList.Semicolon(string);
		}

		@Override
		public Object ofJson(JsonScanner scanner, Type type, Type elementType) throws ConversionException
		{
			if (scanner.getCurrent().getType() != JsonToken.Type.OPEN_ARRAY)
				throw new ConversionException(scanner.getCurrent() + " is not a collection");

			StringList.Semicolon value = new StringList.Semicolon();
			Converter converter = Converter.getConverter(String.class);

			do
			{
				scanner.scan();
				if (scanner.getCurrent().getType() != JsonToken.Type.CLOSE_ARRAY)
					value.add((String) converter.ofJson(scanner, elementType,
						Reflection.getElementType(elementType)));
				else if (!value.isEmpty())
					throw new ConversionException(scanner.getCurrent() + " is not a collection");
			} while (JsonToken.Type.COMMA == scanner.getCurrent().getType());

			if (scanner.getCurrent().getType() != JsonToken.Type.CLOSE_ARRAY)
				throw new ConversionException(scanner.getCurrent() + " is not a collection");

			scanner.scan();
			return value;

		}

		@Override
		public Object readFromResultSet(ResultSet rs, int fields, Class<?> type) throws SQLException, ConversionException
		{
			String value = rs.getString(fields);
			if (rs.wasNull())
				return null;
			return JsonArray.parse(value).stream().map(e -> e.toString())
				.collect(Collectors.toCollection(StringList.Semicolon::new));
		}

		@Override
		public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException, ConversionException
		{
			String value = rs.getString(fields);
			if (rs.wasNull())
				return null;
			return JsonArray.parse(value).stream().map(e -> e.toString())
				.collect(Collectors.toCollection(StringList.Semicolon::new));
		}
	}

	public static class LineBreakConverter extends StringListConverter
	{

		@Override
		public String getDescription()
		{
			return "Lista separada por linhas";
		}

		@Override
		public Object ofString(Class<?> type, String string)
		{
			if (string == null)
				return null;
			return new StringList.LineBreak(string);
		}

		@Override
		public Object ofJson(JsonScanner scanner, Type type, Type elementType) throws ConversionException
		{
			if (scanner.getCurrent().getType() != JsonToken.Type.OPEN_ARRAY)
				throw new ConversionException(scanner.getCurrent() + " is not a collection");

			StringList.LineBreak value = new StringList.LineBreak();
			Converter converter = Converter.getConverter(String.class);

			do
			{
				scanner.scan();
				if (scanner.getCurrent().getType() != JsonToken.Type.CLOSE_ARRAY)
					value.add((String) converter.ofJson(scanner, elementType,
						Reflection.getElementType(elementType)));
				else if (!value.isEmpty())
					throw new ConversionException(scanner.getCurrent() + " is not a collection");
			} while (JsonToken.Type.COMMA == scanner.getCurrent().getType());

			if (scanner.getCurrent().getType() != JsonToken.Type.CLOSE_ARRAY)
				throw new ConversionException(scanner.getCurrent() + " is not a collection");

			scanner.scan();
			return value;

		}

		@Override
		public Object readFromResultSet(ResultSet rs, int fields, Class<?> type) throws SQLException, ConversionException
		{
			String value = rs.getString(fields);
			if (rs.wasNull())
				return null;
			return JsonArray.parse(value).stream().map(e -> e.toString())
				.collect(Collectors.toCollection(StringList.LineBreak::new));
		}

		@Override
		public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException, ConversionException
		{
			String value = rs.getString(fields);
			if (rs.wasNull())
				return null;
			return JsonArray.parse(value).stream().map(e -> e.toString())
				.collect(Collectors.toCollection(StringList.LineBreak::new));
		}
	}

}
