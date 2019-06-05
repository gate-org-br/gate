package gate.converter.collections;

import gate.constraint.Constraint;
import gate.error.ConversionException;
import gate.constraint.Pattern;
import gate.converter.Converter;
import gate.type.collections.IntegerList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collections;
import java.util.List;

public class IntegerListConverter implements Converter
{

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return Collections.singletonList(new Pattern.Implementation("^[ ]*[0-9]+([ ]*(,|;|(\\r?\\n))[ ]*[0-9]+)*[ ]*$"));
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
		return new IntegerList(string);
	}

	@Override
	public Object readFromResultSet(ResultSet rs, int fields, Class<?> type) throws SQLException
	{
		String value = rs.getString(fields);
		if (rs.wasNull())
			return null;
		return new IntegerList(value);
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException
	{
		String value = rs.getString(fields);
		if (rs.wasNull())
			return null;
		return new IntegerList(value);
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

	public static class CommaConverter extends IntegerListConverter
	{

		@Override
		public String getDescription()
		{
			return "Lista separada por vírgulas";
		}

		@Override
		public List<Constraint.Implementation<?>> getConstraints()
		{
			return Collections.singletonList(new Pattern.Implementation("^[ ]*[0-9]+([ ]*,[ ]*[0-9]+)*[ ]*$"));
		}

		@Override
		public Object ofString(Class<?> type, String string)
		{
			if (string == null)
				return null;
			return new IntegerList.Comma(string);
		}

		@Override
		public Object readFromResultSet(ResultSet rs, int fields, Class<?> type) throws SQLException
		{
			String value = rs.getString(fields);
			if (rs.wasNull())
				return null;
			return new IntegerList.Comma(value);
		}

		@Override
		public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException
		{
			String value = rs.getString(fields);
			if (rs.wasNull())
				return null;
			return new IntegerList.Comma(value);
		}
	}

	public static class SemicolonConverter extends IntegerListConverter
	{

		@Override
		public String getDescription()
		{
			return "Lista separada por ponto e vírgula";
		}

		@Override
		public List<Constraint.Implementation<?>> getConstraints()
		{
			return Collections.singletonList(new Pattern.Implementation("^[ ]*[0-9]+([ ]*;[ ]*[0-9]+)*[ ]*$"));
		}

		@Override
		public Object ofString(Class<?> type, String string)
		{
			if (string == null)
				return null;
			return new IntegerList.Semicolon(string);
		}

		@Override
		public Object readFromResultSet(ResultSet rs, int fields, Class<?> type) throws SQLException
		{
			String value = rs.getString(fields);
			if (rs.wasNull())
				return null;
			return new IntegerList.Semicolon(value);
		}

		@Override
		public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException
		{
			String value = rs.getString(fields);
			if (rs.wasNull())
				return null;
			return new IntegerList.Semicolon(value);
		}
	}

	public static class LineBreakConverter extends IntegerListConverter
	{

		@Override
		public String getDescription()
		{
			return "Lista separada por linhas";
		}

		@Override
		public List<Constraint.Implementation<?>> getConstraints()
		{
			return Collections.singletonList(new Pattern.Implementation("^[ ]*[0-9]+([ ]*\\r?\\n[ ]*[0-9]+)*[ ]*$"));
		}

		@Override
		public Object ofString(Class<?> type, String string)
		{
			if (string == null)
				return null;
			return new IntegerList.LineBreak(string);
		}

		@Override
		public Object readFromResultSet(ResultSet rs, int fields, Class<?> type) throws SQLException
		{
			String value = rs.getString(fields);
			if (rs.wasNull())
				return null;
			return new IntegerList.LineBreak(value);
		}

		@Override
		public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException
		{
			String value = rs.getString(fields);
			if (rs.wasNull())
				return null;
			return new IntegerList.LineBreak(value);
		}
	}
}
