package gate.sql;

import java.math.BigDecimal;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

class SQLTypeConverter
{

	public static Class<?> getJavaType(int type)
	{
		switch (type)
		{
			case Types.BIGINT:
			case Types.TINYINT:
			case Types.INTEGER:
			case Types.SMALLINT:
				return Long.class;
			case Types.FLOAT:
			case Types.DOUBLE:
			case Types.DECIMAL:
				return BigDecimal.class;
			case Types.CHAR:
			case Types.NCLOB:
			case Types.NCHAR:
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
			case Types.LONGNVARCHAR:
				return String.class;
			case Types.BLOB:
			case Types.VARBINARY:
			case Types.LONGVARBINARY:
				return byte[].class;
			case Types.DATE:
				return LocalDate.class;
			case Types.TIME:
				return LocalTime.class;
			case Types.TIMESTAMP:
				return LocalDateTime.class;
			default:
				return Object.class;
		}
	}
}
