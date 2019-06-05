package gate.converter.collections;

import gate.constraint.Constraint;
import gate.converter.Converter;
import gate.error.ConversionException;
import gate.type.collections.CharacterList;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collections;
import java.util.List;

public class CharacterListConverter implements Converter
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
		return new CharacterList(string);
	}

	@Override
	public Object readFromResultSet(ResultSet rs, int fields, Class<?> type) throws SQLException
	{
		String value = rs.getString(fields);
		if (rs.wasNull())
			return null;
		return new CharacterList(value);
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException
	{
		String value = rs.getString(fields);
		if (rs.wasNull())
			return null;
		return new CharacterList(value);
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int fields, Object value) throws SQLException
	{
		if (value != null)
			ps.setString(fields++, value.toString());
		else
			ps.setNull(fields++, Types.VARCHAR);
		return fields;
	}

	public static class CommaConverter extends CharacterListConverter
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
			return new CharacterList.Comma(string);
		}

		@Override
		public Object readFromResultSet(ResultSet rs, int fields, Class<?> type) throws SQLException
		{
			String value = rs.getString(fields);
			if (rs.wasNull())
				return null;
			return new CharacterList.Comma(value);
		}

		@Override
		public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException
		{
			String value = rs.getString(fields);
			if (rs.wasNull())
				return null;
			return new CharacterList.Comma(value);
		}
	}

	public static class SemicolonConverter extends CharacterListConverter
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
			return new CharacterList.Semicolon(string);
		}

		@Override
		public Object readFromResultSet(ResultSet rs, int fields, Class<?> type) throws SQLException
		{
			String value = rs.getString(fields);
			if (rs.wasNull())
				return null;
			return new CharacterList.Semicolon(value);
		}

		@Override
		public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException
		{
			String value = rs.getString(fields);
			if (rs.wasNull())
				return null;
			return new CharacterList.Semicolon(value);
		}
	}

	public static class LineBreakConverter extends CharacterListConverter
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
			return new CharacterList.LineBreak(string);
		}

		@Override
		public Object readFromResultSet(ResultSet rs, int fields, Class<?> type) throws SQLException
		{
			String value = rs.getString(fields);
			if (rs.wasNull())
				return null;
			return new CharacterList.LineBreak(value);
		}

		@Override
		public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException
		{
			String value = rs.getString(fields);
			if (rs.wasNull())
				return null;
			return new CharacterList.LineBreak(value);
		}
	}
}
