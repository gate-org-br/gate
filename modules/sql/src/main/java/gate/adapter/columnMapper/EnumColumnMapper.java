package gate.adapter.columnMapper;

import gate.error.ConversionException;
import gate.util.Reflection;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.stream.Stream;


public class EnumColumnMapper implements ColumnMapper
{
	@Override
	public Object readFromResultSet(ResultSet rs, int index, Type type) throws SQLException
	{
		return parse(rs.getString(index), Reflection.getRawType(type));
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Type type) throws SQLException
	{
		return parse(rs.getString(fields), Reflection.getRawType(type));
	}

	private Object parse(String string, Class<?> type)
	{
		if (string == null)
			return null;
		String value = string.trim();
		if (value.isEmpty())
			return null;
		var constantes = type.getEnumConstants();
		if (value.chars().allMatch(Character::isDigit))
			return constantes[Integer.parseInt(value)];
		return Stream.of(constantes)
				.map(Enum.class::cast)
				.filter(e -> e.name().equals(value))
				.findAny()
				.orElseThrow(() -> new ConversionException("%s is not a valid %s enum value".formatted(value, type.getName())));
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int index, Object value) throws SQLException
	{
		if (value instanceof Enum<?> e)
		{
			try
			{
				ps.setString(index, e.name());
			} catch (SQLException ex)
			{
				ps.setInt(index, e.ordinal());
			}
		} else
			ps.setNull(index, Types.NULL);
		return ++index;
	}
}
