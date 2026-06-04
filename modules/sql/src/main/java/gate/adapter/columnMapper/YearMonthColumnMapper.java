package gate.adapter.columnMapper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.YearMonth;


public class YearMonthColumnMapper implements ColumnMapper
{

	@Override
	public Object readFromResultSet(ResultSet rs, int index, Class<?> type) throws SQLException
	{
		LocalDate localDate = rs.getObject(index, LocalDate.class);
		return localDate != null ? YearMonth.of(localDate.getYear(), localDate.getMonth()) : null;
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException
	{
		LocalDate localDate = rs.getObject(fields, LocalDate.class);
		return localDate != null ? YearMonth.of(localDate.getYear(), localDate.getMonth()) : null;
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int index, Object value) throws SQLException
	{
		if (value != null)
		{
			YearMonth yearMonth = (YearMonth) value;
			ps.setObject(index, LocalDate.of(yearMonth.getYear(), yearMonth.getMonth(), 1));
		} else
			ps.setNull(index, Types.DATE);
		return index + 1;
	}
}