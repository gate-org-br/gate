package gate.converter;

import gate.error.ConversionException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class EnumStringConverter extends EnumConverter
{

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? ((Enum) object).name() : "";
	}

	@Override
	public Object readFromResultSet(ResultSet rs, int fields, Class<?> type) throws SQLException, ConversionException
	{
		String value = rs.getString(fields);
		return rs.wasNull() ? null : ofString(type, value);
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException, ConversionException
	{
		String value = rs.getString(fields);
		return rs.wasNull() ? null : ofString(type, value);
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int fields, Object value) throws SQLException
	{
		if (value != null)
			ps.setString(fields++, ((Enum<?>) value).name());
		else
			ps.setNull(fields++, Types.VARCHAR);
		return fields;
	}
}
