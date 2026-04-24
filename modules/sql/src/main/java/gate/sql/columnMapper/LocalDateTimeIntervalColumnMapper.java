package gate.sql.columnMapper;

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


public class LocalDateTimeIntervalColumnMapper extends IntervalColumnMapper
{
	@Override
	public Object readFromResultSet(ResultSet rs, int index, Class<?> type) throws SQLException
	{
		LocalDateTime min = rs.getObject(index, LocalDateTime.class);
		if (rs.wasNull())
			return null;
		LocalDateTime max = rs.getObject(index + 1, LocalDateTime.class);
		if (rs.wasNull())
			return null;
		return LocalDateTimeInterval.of(min, max);
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException
	{
		LocalDateTime min = rs.getObject(fields + ColumnMapper.SEPARATOR + SUFIXES.get(0), LocalDateTime.class);
		if (rs.wasNull())
			return null;
		LocalDateTime max = rs.getObject(fields + ColumnMapper.SEPARATOR + SUFIXES.get(1), LocalDateTime.class);
		if (rs.wasNull())
			return null;
		return LocalDateTimeInterval.of(min, max);
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int index, Object value) throws SQLException
	{
		if (value != null)
		{
			ps.setObject(index++, ((LocalDateTimeInterval) value).getMin());
			ps.setObject(index++, ((LocalDateTimeInterval) value).getMax());
		} else
		{
			ps.setNull(index++, Types.TIMESTAMP);
			ps.setNull(index++, Types.TIMESTAMP);
		}
		return index;
	}
}
