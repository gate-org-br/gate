package gate.adapter.columnMapper;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;


public class PathColumnMapper implements ColumnMapper
{

	@Override
	public Object readFromResultSet(ResultSet rs, int index, Type type) throws SQLException
	{
		String value = rs.getString(index);
		return rs.wasNull() ? null : Path.of(value);
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Type type) throws SQLException
	{
		String value = rs.getString(fields);
		return rs.wasNull() ? null : Path.of(value);
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int index, Object value) throws SQLException
	{
		if (value != null)
			ps.setString(index++, ((Path) value).toString());
		else
			ps.setNull(index++, Types.VARCHAR);
		return index;
	}
}