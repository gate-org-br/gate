package gate.sql.columnMapper;

import gate.error.ConversionException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class EnumOrdinalColumnMapper implements ColumnMapper
{

	@Override
	public Object readFromResultSet(ResultSet rs, int index, Class<?> type) throws SQLException
	{
		int value = rs.getInt(index);
		if (rs.wasNull())
			return null;

		var constants = type.getEnumConstants();
		if (value < 0 || value >= constants.length)
			throw new ConversionException(value + " is not a valid enum ordinal");
		return constants[value];
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException
	{
		int value = rs.getInt(fields);
		if (rs.wasNull())
			return null;

		var constants = type.getEnumConstants();
		if (value < 0 || value >= constants.length)
			throw new ConversionException(value + " is not a valid enum ordinal");
		return constants[value];
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int index, Object value) throws SQLException
	{
		if (value != null)
			ps.setInt(index++, ((Enum<?>) value).ordinal());
		else
			ps.setNull(index++, Types.INTEGER);
		return index;
	}
}
