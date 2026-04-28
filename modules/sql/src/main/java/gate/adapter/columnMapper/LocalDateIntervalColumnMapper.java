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


public class LocalDateIntervalColumnMapper extends IntervalColumnMapper
{
	@Override
	public Object readFromResultSet(ResultSet rs, int index, Class<?> type) throws SQLException
	{
		LocalDate min = rs.getObject(index, LocalDate.class);
		if (rs.wasNull())
			return null;
		LocalDate max = rs.getObject(index + 1, LocalDate.class);
		if (rs.wasNull())
			return null;
		return LocalDateInterval.of(min, max);
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException
	{
		LocalDate min = rs.getObject(fields + ColumnMapper.SEPARATOR + SUFIXES.get(0), LocalDate.class);
		if (rs.wasNull())
			return null;
		LocalDate max = rs.getObject(fields + ColumnMapper.SEPARATOR + SUFIXES.get(1), LocalDate.class);
		if (rs.wasNull())
			return null;
		return LocalDateInterval.of(min, max);
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int index, Object value) throws SQLException
	{
		if (value != null)
		{
			ps.setObject(index++, ((LocalDateInterval) value).getMin());
			ps.setObject(index++, ((LocalDateInterval) value).getMax());
		} else
		{
			ps.setNull(index++, Types.DATE);
			ps.setNull(index++, Types.DATE);
		}
		return index;
	}
}
