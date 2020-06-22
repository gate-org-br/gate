package gate.converter;

import gate.constraint.Constraint;
import gate.error.ConversionException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class PatternConverter implements Converter
{

	@Override
	public String getDescription()
	{
		return "Expressão regular.";
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
		try
		{
			return string != null && string.trim().length() > 0 ? Pattern.compile(string) : null;
		} catch (PatternSyntaxException e)
		{
			throw new ConversionException(String.format("%s não é uma expressão regular válida.", string));
		}
	}

	@Override
	public String toText(Class<?> type, Object object)
	{
		return object != null ? ((Pattern) object).pattern() : "";
	}

	@Override
	public String toText(Class<?> type, Object object, String format)
	{
		return object != null ? String.format(format, ((Pattern) object).pattern()) : "";
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? ((Pattern) object).pattern() : "";
	}

	@Override
	public Object readFromResultSet(ResultSet rs, int fields, Class<?> type) throws SQLException, ConversionException
	{
		String value = rs.getString(fields);
		return rs.wasNull() ? null : ofString(Pattern.class, value);
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException, ConversionException
	{
		String value = rs.getString(fields);
		return rs.wasNull() ? null : ofString(Pattern.class, value);
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int fields, Object value) throws SQLException
	{
		if (value != null)
			ps.setString(fields++, ((Pattern) value).pattern());
		else
			ps.setNull(fields++, Types.VARCHAR);
		return fields;
	}
}
