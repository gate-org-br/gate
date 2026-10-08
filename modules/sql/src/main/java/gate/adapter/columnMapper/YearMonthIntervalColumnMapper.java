package gate.adapter.columnMapper;

import gate.type.LocalDateInterval;
import gate.type.LocalDateTimeInterval;
import gate.type.LocalTimeInterval;
import gate.type.YearMonthInterval;

import java.lang.reflect.Type;
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


public class YearMonthIntervalColumnMapper extends IntervalColumnMapper
{
	@Override
	public Object readFromResultSet(ResultSet rs, int index, Type type) throws SQLException
	{
		YearMonth min = rs.getObject(index, YearMonth.class);
		if (rs.wasNull())
			return null;
		YearMonth max = rs.getObject(index + 1, YearMonth.class);
		if (rs.wasNull())
			return null;
		return YearMonthInterval.of(min, max);
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Type type) throws SQLException
	{
		YearMonth min = rs.getObject(fields + ColumnMapper.SEPARATOR + SUFIXES.get(0), YearMonth.class);
		if (rs.wasNull())
			return null;
		YearMonth max = rs.getObject(fields + ColumnMapper.SEPARATOR + SUFIXES.get(1), YearMonth.class);
		if (rs.wasNull())
			return null;
		return YearMonthInterval.of(min, max);
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int index, Object value) throws SQLException
	{
		if (value != null)
		{
			ps.setObject(index++, ((YearMonthInterval) value).getMin());
			ps.setObject(index++, ((YearMonthInterval) value).getMax());
		} else
		{
			ps.setNull(index++, Types.DATE);
			ps.setNull(index++, Types.DATE);
		}
		return index;
	}
}
