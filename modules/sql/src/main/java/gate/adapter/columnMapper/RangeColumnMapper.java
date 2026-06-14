package gate.adapter.columnMapper;

import gate.type.Range;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.List;


public class RangeColumnMapper implements ColumnMapper
{
	private static final List<String> SUFIXES = Arrays.asList("min", "max");

	@Override
	public List<String> getSufixes()
	{
		return SUFIXES;
	}

	@Override
	public Object readFromResultSet(ResultSet rs, int index, Type type) throws SQLException
	{
		long min = rs.getLong(index);
		if (rs.wasNull())
			return null;
		long max = rs.getLong(index + 1);
		if (rs.wasNull())
			return null;
		return Range.of(min, max);
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Type type) throws SQLException
	{
		long min = rs.getLong(fields + ColumnMapper.SEPARATOR + SUFIXES.get(0));
		if (rs.wasNull())
			return null;
		long max = rs.getLong(fields + ColumnMapper.SEPARATOR + SUFIXES.get(1));
		if (rs.wasNull())
			return null;
		return Range.of(min, max);
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int index, Object value) throws SQLException
	{
		if (value != null)
		{
			ps.setLong(index++, ((Range) value).getMin());
			ps.setLong(index++, ((Range) value).getMax());
		} else
		{
			ps.setNull(index++, Types.INTEGER);
			ps.setNull(index++, Types.INTEGER);
		}
		return index;
	}
}
