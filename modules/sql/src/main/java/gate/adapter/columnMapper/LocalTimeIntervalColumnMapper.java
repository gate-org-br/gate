package gate.adapter.columnMapper;

import gate.type.LocalDateInterval;
import gate.type.LocalDateTimeInterval;
import gate.type.LocalTimeInterval;
import gate.type.YearMonthInterval;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;


public class LocalTimeIntervalColumnMapper extends IntervalColumnMapper
{
	@Override
	public Object readFromResultSet(ResultSet rs, int index, Class<?> type) throws SQLException
	{
		LocalTime min = rs.getObject(index, LocalTime.class);
		if (rs.wasNull())
			return null;
		LocalTime max = rs.getObject(index + 1, LocalTime.class);
		if (rs.wasNull())
			return null;
		return LocalTimeInterval.of(min, max);
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException
	{
		LocalTime min = rs.getObject(fields + ColumnMapper.SEPARATOR + SUFIXES.get(0), LocalTime.class);
		if (rs.wasNull())
			return null;
		LocalTime max = rs.getObject(fields + ColumnMapper.SEPARATOR + SUFIXES.get(1), LocalTime.class);
		if (rs.wasNull())
			return null;
		return LocalTimeInterval.of(min, max);
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int index, Object value) throws SQLException
	{
		if (value != null)
		{
			ps.setObject(index++, ((LocalTimeInterval) value).getMin());
			ps.setObject(index++, ((LocalTimeInterval) value).getMax());
		} else
		{
			ps.setNull(index++, Types.TIME);
			ps.setNull(index++, Types.TIME);
		}
		return index;
	}
}
