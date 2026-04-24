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


public abstract class IntervalColumnMapper implements ColumnMapper
{
	protected static final List<String> SUFIXES = Arrays.asList("min", "max");

	@Override
	public List<String> getSufixes()
	{
		return SUFIXES;
	}
}
