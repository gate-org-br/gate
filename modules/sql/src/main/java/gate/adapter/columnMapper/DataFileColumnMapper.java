package gate.adapter.columnMapper;

import gate.type.DataFile;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.List;


public class DataFileColumnMapper implements ColumnMapper
{
	private static final List<String> SUFIXES = Arrays.asList("name", "size", "data");

	@Override
	public List<String> getSufixes()
	{
		return SUFIXES;
	}

	@Override
	public Object readFromResultSet(ResultSet rs, int index, Class<?> type) throws SQLException
	{
		String name = rs.getString(index);
		if (rs.wasNull())
			return null;
		rs.getLong(index + 1);
		if (rs.wasNull())
			return null;
		byte[] data = rs.getBytes(index + 2);
		if (rs.wasNull())
			return null;
		return DataFile.of(data, name);
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException
	{
		String name = rs.getString(fields + ColumnMapper.SEPARATOR + SUFIXES.get(0));
		if (rs.wasNull())
			return null;
		rs.getLong(fields + ColumnMapper.SEPARATOR + SUFIXES.get(1));
		if (rs.wasNull())
			return null;
		byte[] data = rs.getBytes(fields + ColumnMapper.SEPARATOR + SUFIXES.get(2));
		if (rs.wasNull())
			return null;
		return DataFile.of(data, name);
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int index, Object value) throws SQLException
	{
		if (value != null)
		{
			DataFile dataFile = (DataFile) value;
			ps.setString(index++, dataFile.getName());
			ps.setLong(index++, dataFile.getData().length);
			ps.setBytes(index++, dataFile.getData());
		} else
		{
			ps.setNull(index++, Types.VARCHAR);
			ps.setNull(index++, Types.INTEGER);
			ps.setNull(index++, Types.BINARY);
		}
		return index;
	}
}