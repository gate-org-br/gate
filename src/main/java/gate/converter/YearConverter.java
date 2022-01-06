package gate.converter;

import gate.constraint.Constraint;
import gate.error.ConversionException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.Year;
import java.time.format.DateTimeParseException;
import java.util.List;

public class YearConverter implements Converter
{

	@Override
	public String getMask()
	{
		return "####";
	}

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return List.of();
	}

	@Override
	public String getDescription()
	{
		return "Campos de mês/ano devem ser preenchidos no formato YYYY";
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

	@Override
	public String toText(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

	@Override
	public String toText(Class<?> type, Object object, String format)
	{
		return object != null ? object.toString() : "";
	}

	@Override
	public Object ofString(Class<?> type, String string) throws ConversionException
	{
		if (string == null)
			return null;

		string = string.trim();
		if (string.isEmpty())
			return null;

		try
		{
			return Year.parse(string);
		} catch (DateTimeParseException ex)
		{
			throw new ConversionException(ex, "%s não é um ano válido.", ex.getParsedString(), getDescription());
		}
	}

	@Override
	public Object readFromResultSet(ResultSet rs, int index,
		Class<?> type) throws SQLException
	{
		int year = rs.getInt(index);
		return rs.wasNull() ? null : Year.of(year);
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException
	{
		int year = rs.getInt(fields);
		return rs.wasNull() ? null : Year.of(year);
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int index, Object value) throws SQLException
	{
		if (value != null)
			ps.setObject(index, ((Year) value).getValue());
		else
			ps.setNull(index, Types.INTEGER);
		return index + 1;
	}

}
