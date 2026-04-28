package gate.adapter.columnMapper;

import gate.adapter.converter.Converter;
import gate.error.ConversionException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.stream.Stream;


public class EnumColumnMapper implements ColumnMapper
{

	@Override
	public Object readFromResultSet(ResultSet rs, int index, Class<?> type) throws SQLException
	{
		String string = rs.getString(index);
		if (rs.wasNull())
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
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException
	{
		String string = rs.getString(fields);
		if (rs.wasNull())
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
		var string = Converter.toString(value);
		if (string.isEmpty())
			ps.setNull(index++, Types.NULL);
		else if (string.chars().allMatch(Character::isDigit))
			ps.setInt(index++, Integer.parseInt(string));
		else
			ps.setString(index++, string);
		return index;
	}
}