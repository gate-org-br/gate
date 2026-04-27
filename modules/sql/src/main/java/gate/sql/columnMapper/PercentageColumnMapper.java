package gate.sql.columnMapper;

import gate.type.Percentage;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class PercentageColumnMapper implements ColumnMapper
{

	@Override
	public Object readFromResultSet(ResultSet rs, int index, Class<?> type) throws SQLException
	{
		BigDecimal value = rs.getBigDecimal(index);
		return rs.wasNull() ? null : new Percentage(value);
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException
	{
		BigDecimal value = rs.getBigDecimal(fields);
		return rs.wasNull() ? null : new Percentage(value);
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int index, Object value) throws SQLException
	{
		if (value != null)
			ps.setBigDecimal(index++, ((Percentage) value).getValue());
		else
			ps.setNull(index++, Types.DECIMAL);
		return index;
	}
}
