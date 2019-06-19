package gate.lang.property;

import gate.error.PropertyError;
import gate.annotation.Schema;
import gate.annotation.Table;
import gate.sql.condition.Condition;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EntityInfo
{

	private final Class<?> type;
	private final String name;
	private final String fullName;
	private final String tableName;
	private final PropertyInfo id;
	private final String packageName;
	private final String parentPackageName;
	private final List<PropertyInfo> properties;
	private static final Map<Property, List<String>> JOINS = new ConcurrentHashMap<>();
	private static final Map<Property, String> FULL_COLUMN_NAMES = new ConcurrentHashMap<>();

	public EntityInfo(Class<?> type)
	{
		this.type = type;
		this.fullName = type.getName();
		this.name = type.getSimpleName();
		this.tableName = EntityInfo.getTableName(type);
		this.packageName = type.getPackageName();
		this.id = new PropertyInfo(Property.getProperty(type, EntityInfo.getId(type)));
		this.properties = EntityInfo.getProperties(type, e -> !e.isEntityId()).stream()
			.map(e -> new PropertyInfo(e)).collect(Collectors.toList());

		String[] names = this.packageName.split("[.]");
		this.parentPackageName = Stream.of(names).limit(names.length - 1).collect(Collectors.joining("."));
	}

	public Class<?> getType()
	{
		return type;
	}

	public String getName()
	{
		return name;
	}

	public String getFullName()
	{
		return fullName;
	}

	public String getTableName()
	{
		return tableName;
	}

	public String getPackageName()
	{
		return packageName;
	}

	public String getParentPackageName()
	{
		return parentPackageName;
	}

	public PropertyInfo getId()
	{
		return id;
	}

	public List<PropertyInfo> getProperties()
	{
		return properties;
	}

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
						name = name + "." + EntityInfo.getId(type);

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
		return getProperties(type)
			.stream().map(Property::toString)
			.map(e -> "=" + e)
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
					path.add(attribute.getTableName());
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
					path.add(attribute.getTableName());
				} else
				{
					name.add(attribute.getColumnName());
					if (attribute.isEntity())
					{
						String id = EntityInfo.getId(attribute.getRawType());
						String FK = path + "." + attribute.getColumnName() + "$" + id;
						path.merge(name);
						name = new StringJoiner("$");
						String PK = path + "." + id;
						joins.add("left join " + attribute.getFullTableName() + " as " + path + " on " + Condition.of(FK).isEq(PK));
					}
				}
			}
			return Collections.unmodifiableList(joins);
		});
	}

	public static class PropertyInfo
	{

		private final String type;
		private final String name;
		private final String getter;
		private final String columnName;
		private final String setterName;
		private final String typeFullName;

		PropertyInfo(Property property)
		{
			this.columnName = property.getColumnName();
			this.typeFullName = property.getRawType().getName();
			this.type = property.getRawType().getSimpleName();

			this.name = property
				.getAttributes()
				.stream()
				.skip(1)
				.map(e -> e.toString())
				.collect(Collectors.joining("."));

			this.getter = property.getAttributes()
				.stream()
				.skip(1)
				.map(e -> e.toString())
				.map(e -> "get" + Character.toUpperCase(e.charAt(0)) + e.substring(1) + "()")
				.collect(Collectors.joining("."));

			this.setterName = property.getAttributes()
				.stream()
				.skip(1)
				.limit(1)
				.map(e -> e.toString())
				.map(e -> "set" + Character.toUpperCase(e.charAt(0)) + e.substring(1))
				.collect(Collectors.joining("."));
		}

		public String getColumnName()
		{
			return columnName;
		}

		public String getType()
		{
			return type;
		}

		public String getGetter()
		{
			return getter;
		}

		public String getSetterName()
		{
			return setterName;
		}

		public String getTypeFullName()
		{
			return typeFullName;
		}

		public String getName()
		{
			return name;
		}
	}
}
