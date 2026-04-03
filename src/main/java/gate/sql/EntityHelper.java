package gate.sql;

import gate.annotation.Schema;
import gate.annotation.Table;
import gate.error.PropertyError;

public class EntityHelper
{
	public static boolean isEntity(Class<?> type)
	{
		return type.isAnnotationPresent(gate.annotation.Entity.class);
	}

	public static void check(Class<?> type)
	{
		if (!isEntity(type))
			throw new PropertyError("%s is not an Entity", type.getName());
	}

	public static String getId(Class<?> type)
	{
		check(type);
		return type.getAnnotation(gate.annotation.Entity.class).value();
	}

	public static String getTableName(Class<?> type)
	{
		check(type);
		return type.isAnnotationPresent(Table.class) ? type.getAnnotation(Table.class).value()
				: type.getSimpleName();
	}

	public static String getFullTableName(Class<?> type)
	{
		check(type);
		return type.isAnnotationPresent(Schema.class)
				? type.getAnnotation(Schema.class).value() + "." + getTableName(type)
				: getTableName(type);
	}
}