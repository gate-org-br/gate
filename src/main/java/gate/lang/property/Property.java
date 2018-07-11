package gate.lang.property;

import gate.constraint.Constraint;
import gate.converter.Converter;
import gate.entity.User;
import gate.sql.condition.Condition;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Property
{

	private final String string;
	private final Class<?> type;
	private volatile List<String> joins;
	private volatile String fullColumnName;
	private final List<Attribute> attributes;
	private final static Map<Class<?>, Map<String, Property>> PROPERTIES = new ConcurrentHashMap<>();

	Property(Class<?> type, String string, List<Attribute> attributes)
	{
		this.type = type;
		this.string = string;
		this.attributes = Collections.unmodifiableList(attributes);
	}

	/**
	 * Selects a property from the specified java class.
	 *
	 * @param type java class whose property is to be selected
	 * @param name name of the property to be selected
	 *
	 * @return the requested property object or null if there is no property with the specified name on the specified
	 * type
	 */
	public static Property parse(Class<?> type, String name)
	{
		return PROPERTIES
				.computeIfAbsent(type, k -> new ConcurrentHashMap<>())
				.computeIfAbsent(name, k -> new PropertyParser(type, k).parse());
	}

	/**
	 * Selects a property from the specified type.
	 *
	 * @param type type whose property is to be selected
	 * @param name name of the property to be selected
	 *
	 * @return the requested property object
	 *
	 * @throws NoSuchPropertyError if there is no property with the specified name on the specified type
	 */
	public static Property getProperty(Class<?> type, String name)
	{
		Property property = parse(type, name);
		if (property == null)
			throw new NoSuchPropertyError(type, name);
		return property;
	}

	/**
	 * Selects all first level properties of the specified java class.
	 *
	 * @param type java class whose properties are to be selected
	 *
	 * @return all first level properties of the specified java class
	 */
	public static List<Property> getProperties(Class<?> type)
	{
		List<Property> properties = new ArrayList<>();

		while (type != null)
		{
			for (Field field : type.getDeclaredFields())
				if (!Modifier.isTransient(field.getModifiers())
						&& !Modifier.isStatic(field.getModifiers()))
					properties.add(Property.getProperty(type, field.getName()));
			type = type.getSuperclass();
		}

		return properties;
	}

	/**
	 * Selects a list of properties from the specified java class.
	 *
	 * @param type java class whose properties are to be selected
	 * @param names names of the properties to be selected
	 *
	 * @return the requested properties
	 *
	 * @throws NoSuchPropertyError if there is no property with any of the specified names on the specified java class
	 */
	public static List<Property> getProperties(Class<?> type, String... names)
	{
		return getProperties(type, Arrays.asList(names));
	}

	/**
	 * Selects a list of properties from the specified java class.
	 *
	 * @param type java class whose properties are to be selected
	 * @param names names of the properties to be selected
	 *
	 * @return the requested properties
	 *
	 * @throws NoSuchPropertyError if there is no property with any of the specified names on the specified java class
	 */
	public static List<Property> getProperties(Class<?> type, List<String> names)
	{
		return names
				.stream()
				.map(e -> getProperty(type, e))
				.collect(Collectors.toList());
	}

	/**
	 * Reads the property value from the specified object.
	 *
	 * @param object object whose value is to be read
	 *
	 * @return the requested value
	 */
	public Object getValue(Object object)
	{
		for (int i = 0; i < attributes.size() && object != null; i++)
			object = attributes.get(i).getValue(object);
		return object;
	}

	/**
	 * Updates the property value of the specified object.
	 *
	 * @param object object whose value is to be updated
	 * @param value the new value
	 */
	public void setValue(Object object, Object value)
	{
		for (int i = 0; i < attributes.size() - 1; i++)
			object = attributes.get(i).forceValue(object);
		attributes.get(attributes.size() - 1).setValue(object, value);
	}

	/**
	 * Checks if the specified object has a value for this property.
	 *
	 * @param object object to be checked
	 *
	 * @return true if there is a value for this property or false otherwise
	 */
	public boolean isEmpty(Object object)
	{
		return getValue(object) != null;
	}

	/**
	 * Gets a list of all constraints defined for this property.
	 *
	 * @return a list of all constraints defined for this property
	 * @see gate.constraint.Constraint
	 */
	public Collection<Constraint.Implementation> getConstraints()
	{
		return getLastAttribute().getConstraints();
	}

	public Optional<String> getName()
	{
		return getLastAttribute().getName();
	}

	public String getDescription()
	{
		return getLastAttribute().getDescription();
	}

	public String getMask()
	{
		return getLastAttribute().getMask();
	}

	public Class<?> getRawType()
	{
		return getLastAttribute().getRawType();
	}

	public Type getType()
	{
		return getLastAttribute().getType();
	}

	public List<String> getJoins()
	{
		List<String> joins = this.joins;
		if (joins == null)
			synchronized (this)
			{
				joins = this.joins;
				if (joins == null)
				{
					List<String> _joins = new ArrayList<>();
					StringJoiner name = new StringJoiner("$");
					StringJoiner path = new StringJoiner("$");
					for (Attribute attribute : attributes)
					{
						if (attribute instanceof SelfAttribute)
						{
							path.add(attribute.getTableName());
						} else
						{
							name.add(attribute.getColumnName());
							if (attribute.isEntity())
							{
								String id = Entity.getId(attribute.getRawType());
								String FK = path + "." + attribute.getColumnName() + "$" + id;
								path.merge(name);
								name = new StringJoiner("$");
								String PK = path + "." + id;
								_joins.add(
										"left join " + attribute.getFullTableName() + " as " + path + " on " + Condition
										.of(FK).isEq(PK));
							}
						}
					}
					this.joins = joins = Collections.unmodifiableList(_joins);
				}
			}
		return joins;
	}

	public String getFullColumnName()
	{
		String fullColumnName = this.fullColumnName;
		if (fullColumnName == null)
		{
			synchronized (this)
			{
				fullColumnName = this.fullColumnName;
				if (fullColumnName == null)
				{
					StringJoiner name = new StringJoiner("$");
					StringJoiner path = new StringJoiner("$");
					for (Attribute attribute : getAttributes())
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
					this.fullColumnName = fullColumnName = path + "." + name;
				}
			}
		}

		return fullColumnName;
	}

	public Type getElementType()
	{
		return getLastAttribute().getElementType();
	}

	public Class<?> getElementRawType()
	{
		return getLastAttribute().getElementRawType();
	}

	public List<Attribute> getAttributes()
	{
		return Collections.unmodifiableList(attributes);
	}

	public boolean isEntityId()
	{
		return getAttributes().size() == 2
				&& getAttributes().get(1).isEntityId();
	}

	public static Object getValue(Object object, String name)
	{
		return object != null
				? Property.getProperty(object.getClass(), name)
						.getValue(object) : null;
	}

	public Converter getConverter()
	{
		return getLastAttribute().getConverter();
	}

	public Attribute getLastAttribute()
	{
		return attributes.get(attributes.size() - 1);
	}

	public Property getPreviousProperty()
	{
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < attributes.size() - 1; i++)
		{
			if (!(attributes.get(i) instanceof SelfAttribute))
			{
				if (builder.length() > 0
						&& attributes.get(i) instanceof JavaIdentifierAttribute)
					builder.append(".");
				builder.append(attributes.get(i).toString());
			}
		}
		return new Property(type, builder.toString(), attributes.subList(0, attributes.size() - 1));
	}

	public String getColumnName()
	{
		return getAttributes().stream().map(e -> e.getColumnName())
				.filter(e -> e != null).collect(Collectors.joining("$"));
	}

	@Override
	public boolean equals(Object object)
	{
		return object instanceof Property
				&& object == this;
	}

	@Override
	public int hashCode()
	{
		return toString().hashCode();
	}

	@Override
	public String toString()
	{
		return string;
	}
}
