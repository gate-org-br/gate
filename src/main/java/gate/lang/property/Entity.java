package gate.lang.property;

import gate.annotation.Schema;
import gate.annotation.Table;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class Entity
{

	public static boolean isEntity(Class type)
	{
		return type.isAnnotationPresent(gate.annotation.Entity.class);
	}

	public static void check(Class type)
	{
		if (!isEntity(type))
			throw new PropertyError("%s is not an Entity", type.getName());
	}

	public static String getId(Class type)
	{
		check(type);
		return ((gate.annotation.Entity) type.getAnnotation(gate.annotation.Entity.class)).value();
	}

	public static String getTableName(Class<?> type)
	{
		check(type);
		return type.isAnnotationPresent(Table.class)
				? type.getAnnotation(Table.class).value()
				: type.getSimpleName();
	}

	public static String getFullTableName(Class<?> type)
	{
		check(type);
		return type.isAnnotationPresent(Schema.class)
				? type.getAnnotation(Schema.class).value()
				+ "." + getTableName(type) : getTableName(type);
	}

	public static List<Property> getProperties(Class<?> type, Predicate<Property> predicate)
	{
		List<Property> properties = new ArrayList<>();

		while (isEntity(type))
		{
			for (Field field : type.getDeclaredFields())
			{
				if (!field.getType().isArray()
						&& !Modifier.isStatic(field.getModifiers())
						&& !Modifier.isTransient(field.getModifiers())
						&& !Collection.class.isAssignableFrom(field.getType()))
				{
					String name = field.getName();
					if (isEntity(field.getType()))
						name = name + "." + Entity.getId(type);

					Property property = Property.getProperty(type, name);

					if (predicate.test(property))
						properties.add(property);
				}
			}
			type = type.getSuperclass();
		}

		return properties;
	}

	public static Stream<String> getColumnNames(Class<?> type, String name)
	{
		Property property = Property.getProperty(type, name);
		return property.getConverter().getColumns(property.getColumnName());
	}

	public static Stream<String> getFullColumnNames(Class<?> type, String name)
	{
		Property property = Property.getProperty(type, name);
		return property.getConverter().getColumns(property.getFullColumnName());
	}
}
