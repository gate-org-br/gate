package gate.converter.custom;

import gate.constraint.Constraint;
import gate.error.ConversionException;
import gate.constraint.Pattern;
import gate.converter.Converter;
import gate.type.Data;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class DataConverter implements Converter
{

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		List<Constraint.Implementation<?>> constraints = new ArrayList<>();
		constraints.add(new Pattern.Implementation("^\\s*[0-9]+([,][0-9]{1,2})?\\s*[BbKkMmGgTtPpEeZzYy]\\s*$"));
		return constraints;
	}

	@Override
	public String getDescription()
	{
		return "Entre com um número de no máximo duas casas decimais seguido da unidade correspondente (B, K, M, G, T, P, E, Z ou Y)";
	}

	@Override
	public String getMask()
	{
		return null;
	}

	@Override
	public Object ofString(Class<?> type, String string) throws ConversionException
	{
		try
		{
			if (string == null || string.trim().isEmpty())
				return null;
			return new Data(string);
		} catch (IllegalArgumentException e)
		{
			throw new ConversionException(String.format("%s não representa uma quantidade de dados válida.", string));
		}

	}

	@Override
	public String toText(Class<?> type, Object object)
	{
		if (object == null)
			return "";
		return object.toString();
	}

	@Override
	public String toText(Class<?> type, Object object, String format)
	{
		if (object == null)
			return "";
		return String.format(format, object);
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		if (object == null)
			return "";
		return object.toString();
	}

	@Override
	public Object readFromResultSet(ResultSet rs, int fields, Class<?> type) throws SQLException
	{
		BigDecimal value = rs.getBigDecimal(fields);
		return rs.wasNull() ? null : new Data(value);
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException
	{
		BigDecimal value = rs.getBigDecimal(fields);
		return rs.wasNull() ? null : new Data(value);
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int fields, Object value) throws SQLException
	{
		if (value != null)
			ps.setBigDecimal(fields++, ((Data) value).getBytes());
		else
			ps.setNull(fields++, Types.DECIMAL);
		return fields;
	}
}
