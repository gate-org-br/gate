package gate.sql;

import gate.annotation.Name;
import gate.annotation.Schema;
import gate.annotation.Table;
import gate.error.PropertyError;
import gate.lang.property.Attribute;
import gate.lang.property.Property;
import gate.lang.property.SelfAttribute;
import gate.sql.condition.Condition;
import gate.type.ID;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class EntityHelper
{

	private static final Map<Property, List<String>> JOINS = new ConcurrentHashMap<>();
	private static final Map<Property, String> FULL_COLUMN_NAMES = new ConcurrentHashMap<>();

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

	public static String getDisplayName(Class<?> type)
	{
		check(type);
		return Name.Extractor.extract(type).orElse("Unnamed");
	}

	public static String getFullTableName(Class<?> type)
	{
		check(type);
		return type.isAnnotationPresent(Schema.class)
				? type.getAnnotation(Schema.class).value() + "." + getTableName(type)
				: getTableName(type);
	}

	public static List<Property> getProperties(Class<?> type, Predicate<Property> predicate)
	{
		List<Property> properties = new ArrayList<>();

		while (isEntity(type))
		{
			for (Field field : type.getDeclaredFields())
			{
				if (!field.getType().isArray() && !Modifier.isStatic(field.getModifiers())
						&& !Modifier.isTransient(field.getModifiers())
						&& !Collection.class.isAssignableFrom(field.getType()))
				{
					String name = field.getName();
					if (isEntity(field.getType()))
						name = name + "." + EntityHelper.getId(type);

					Property property = Property.getProperty(type, name);

					if (predicate.test(property))
						properties.add(property);
				}
			}
			type = type.getSuperclass();
		}

		return properties;
	}

	public static List<Property> getProperties(Class<?> type)
	{
		return getProperties(type, e -> true);
	}

	public static String[] getFullGQN(Class<?> type)
	{
		return getProperties(type).stream().map(Property::toString).map(e -> "=" + e)
				.toArray(String[]::new);
	}

	public static Stream<String> getColumnNames(Class<?> type, String name)
	{
		Property property = Property.getProperty(type, name);
		return property.getConverter().getColumns(property.getColumnName());
	}

	public static Stream<String> getFullColumnNames(Class<?> type, String name)
	{
		Property property = Property.getProperty(type, name);
		return property.getConverter().getColumns(getFullColumnName(property));
	}

	public static String getFullColumnName(Property property)
	{
		return FULL_COLUMN_NAMES.computeIfAbsent(property, e ->
		{
			StringJoiner name = new StringJoiner("$");
			StringJoiner path = new StringJoiner("$");
			for (Attribute attribute : e.getAttributes())
			{
				if (attribute instanceof SelfAttribute)
				{
					path.add(getTableName(attribute.getRawType()));
				} else
				{
					name.add(attribute.getColumnName());
					if (attribute.isEntity())
					{
						path.merge(name);
						name = new StringJoiner("$");
					}
				}
			}

			return path + "." + name;
		});
	}

	public static List<String> getJoins(Property property)
	{
		return JOINS.computeIfAbsent(property, e ->
		{
			List<String> joins = new ArrayList<>();
			StringJoiner name = new StringJoiner("$");
			StringJoiner path = new StringJoiner("$");
			for (Attribute attribute : e.getAttributes())
			{
				if (attribute instanceof SelfAttribute)
				{
					path.add(getTableName(attribute.getRawType()));
				} else
				{
					name.add(attribute.getColumnName());
					if (attribute.isEntity())
					{
						String id = EntityHelper.getId(attribute.getRawType());
						String FK = path + "." + attribute.getColumnName() + "$" + id;
						path.merge(name);
						name = new StringJoiner("$");
						String PK = path + "." + id;
						joins.add("left join " + getFullTableName(attribute.getRawType()) + " as " + path
								+ " on " + Condition.of(FK).isEq(PK));
					}
				}
			}
			return Collections.unmodifiableList(joins);
		});
	}

	public static <T> T create(Class<T> type, String id)
			throws ReflectiveOperationException
	{
		T object = type.getConstructor().newInstance();
		Property.getProperty(type, EntityHelper.getId(type)).setValue(object, ID.valueOf(id));
		return object;
	}
}
