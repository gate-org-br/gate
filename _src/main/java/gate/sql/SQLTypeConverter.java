package gate.sql;

import gate.type.Date;
import gate.type.DateTime;
import gate.type.Time;
import java.math.BigDecimal;
import java.sql.Types;

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
				return Date.class;
			case Types.TIME:
				return Time.class;
			case Types.TIMESTAMP:
				return DateTime.class;
			default:
				return Object.class;
		}
	}
}
