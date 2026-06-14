package gate.adapter.columnMapper;

import gate.type.Tax;

import java.math.BigDecimal;
import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class TaxColumnMapper implements ColumnMapper
{

	@Override
	public Object readFromResultSet(ResultSet rs, int index, Type type) throws SQLException
	{
		BigDecimal value = rs.getBigDecimal(index);
		return rs.wasNull() ? null : Tax.valueOf(value);
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Type type) throws SQLException
	{
		BigDecimal value = rs.getBigDecimal(fields);
		return rs.wasNull() ? null : Tax.valueOf(value);
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int index, Object value) throws SQLException
	{
		if (value != null)
			ps.setBigDecimal(index++, ((Tax) value).getValue());
		else
			ps.setNull(index++, Types.DECIMAL);
		return index;
	}
}
