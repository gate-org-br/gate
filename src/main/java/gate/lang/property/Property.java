package gate.lang.property;

import gate.constraint.Constraint;
import gate.converter.Converter;
import gate.error.NoSuchPropertyError;
import gate.util.Icons;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Property
{

	private final String string;
	private final Class<?> owner;
	private final Attribute lastAttribute;
	private final List<Attribute> attributes;
	private final static Map<Class<?>, Map<String, Property>> PROPERTIES
		= new ConcurrentHashMap<>();

	Property(Class<?> owner, String string, List<Attribute> attributes)
	{
		this.owner = owner;
		this.string = string;
		this.attributes = Collections.unmodifiableList(attributes);
		this.lastAttribute = attributes.get(attributes.size() - 1);
	}

	/**
	 * Selects a property from the specified java class.
	 *
	 * @param type java class whose property is to be selected
	 * @param name name of the property to be selected
	 *
	 * @return the requested property object or null if there is no property with the specified name on the specified type
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
	 * Reads the property value from the specified object.
	 *
	 * @param object object whose value is to be read
	 *
	 * @return the requested value
	 *
	 * @throws java.lang.UnsupportedOperationException if the property is not compatible with a boolean value
	 */
	public boolean getBoolean(Object object)
	{
		for (int i = 0; i < attributes.size() && object != null; i++)
			object = attributes.get(i).getValue(object);
		return attributes.get(attributes.size() - 1).getBoolean(object);
	}

	/**
	 * Updates the property value of the specified boolean value.
	 *
	 * @param object object whose value is to be updated
	 * @param value the new value
	 *
	 * @throws java.lang.UnsupportedOperationException if the property is not compatible with a boolean value
	 */
	public void setBoolean(Object object, boolean value)
	{
		for (int i = 0; i < attributes.size() - 1; i++)
			object = attributes.get(i).forceValue(object);
		attributes.get(attributes.size() - 1).setBoolean(object, value);
	}

	/**
	 * Reads the property value from the specified object.
	 *
	 * @param object object whose value is to be read
	 *
	 * @return the requested value
	 *
	 * @throws java.lang.UnsupportedOperationException if the property is not compatible with a char value
	 */
	public char getChar(Object object)
	{
		for (int i = 0; i < attributes.size() && object != null; i++)
			object = attributes.get(i).getValue(object);
		return attributes.get(attributes.size() - 1).getChar(object);
	}

	/**
	 * Updates the property value of the specified char value.
	 *
	 * @param object object whose value is to be updated
	 * @param value the new value
	 *
	 * @throws java.lang.UnsupportedOperationException if the property is not compatible with a char value
	 */
	public void setChar(Object object, char value)
	{
		for (int i = 0; i < attributes.size() - 1; i++)
			object = attributes.get(i).forceValue(object);
		attributes.get(attributes.size() - 1).setChar(object, value);
	}

	/**
	 * Reads the property value from the specified object.
	 *
	 * @param object object whose value is to be read
	 *
	 * @return the requested value
	 *
	 * @throws java.lang.UnsupportedOperationException if the property is not compatible with a byte value
	 */
	public byte getByte(Object object)
	{
		for (int i = 0; i < attributes.size() && object != null; i++)
			object = attributes.get(i).getValue(object);
		return attributes.get(attributes.size() - 1).getByte(object);
	}

	/**
	 * Updates the property value of the specified byte value.
	 *
	 * @param object object whose value is to be updated
	 * @param value the new value
	 *
	 * @throws java.lang.UnsupportedOperationException if the property is not compatible with a byte value
	 */
	public void setByte(Object object, byte value)
	{
		for (int i = 0; i < attributes.size() - 1; i++)
			object = attributes.get(i).forceValue(object);
		attributes.get(attributes.size() - 1).setByte(object, value);
	}

	/**
	 * Reads the property value from the specified object.
	 *
	 * @param object object whose value is to be read
	 *
	 * @return the requested value
	 *
	 * @throws java.lang.UnsupportedOperationException if the property is not compatible with a short value
	 */
	public short getShort(Object object)
	{
		for (int i = 0; i < attributes.size() && object != null; i++)
			object = attributes.get(i).getValue(object);
		return attributes.get(attributes.size() - 1).getShort(object);
	}

	/**
	 * Updates the property value of the specified short value.
	 *
	 * @param object object whose value is to be updated
	 * @param value the new value
	 *
	 * @throws java.lang.UnsupportedOperationException if the property is not compatible with a short value
	 */
	public void setShort(Object object, short value)
	{
		for (int i = 0; i < attributes.size() - 1; i++)
			object = attributes.get(i).forceValue(object);
		attributes.get(attributes.size() - 1).setShort(object, value);
	}

	/**
	 * Reads the property value from the specified object.
	 *
	 * @param object object whose value is to be read
	 *
	 * @return the requested value
	 *
	 * @throws java.lang.UnsupportedOperationException if the property is not compatible with an int value
	 */
	public int getInt(Object object)
	{
		for (int i = 0; i < attributes.size() && object != null; i++)
			object = attributes.get(i).getValue(object);
		return attributes.get(attributes.size() - 1).getInt(object);
	}

	/**
	 * Updates the property value of the specified int value.
	 *
	 * @param object object whose value is to be updated
	 * @param value the new value
	 *
	 * @throws java.lang.UnsupportedOperationException if the property is not compatible with an int value
	 */
	public void setInt(Object object, int value)
	{
		for (int i = 0; i < attributes.size() - 1; i++)
			object = attributes.get(i).forceValue(object);
		attributes.get(attributes.size() - 1).setInt(object, value);
	}

	/**
	 * Reads the property value from the specified object.
	 *
	 * @param object object whose value is to be read
	 *
	 * @return the requested value
	 *
	 * @throws java.lang.UnsupportedOperationException if the property is not compatible with a long value
	 */
	public long getLong(Object object)
	{
		for (int i = 0; i < attributes.size() && object != null; i++)
			object = attributes.get(i).getValue(object);
		return attributes.get(attributes.size() - 1).getLong(object);
	}

	/**
	 * Updates the property value of the specified long value.
	 *
	 * @param object object whose value is to be updated
	 * @param value the new value
	 *
	 * @throws java.lang.UnsupportedOperationException if the property is not compatible with a long value
	 */
	public void setLong(Object object, long value)
	{
		for (int i = 0; i < attributes.size() - 1; i++)
			object = attributes.get(i).forceValue(object);
		attributes.get(attributes.size() - 1).setLong(object, value);
	}

	/**
	 * Reads the property value from the specified object.
	 *
	 * @param object object whose value is to be read
	 *
	 * @return the requested value
	 *
	 * @throws java.lang.UnsupportedOperationException if the property is not compatible with a float value
	 */
	public float getFloat(Object object)
	{
		for (int i = 0; i < attributes.size() && object != null; i++)
			object = attributes.get(i).getValue(object);
		return attributes.get(attributes.size() - 1).getFloat(object);
	}

	/**
	 * Updates the property value of the specified float value.
	 *
	 * @param object object whose value is to be updated
	 * @param value the new value
	 *
	 * @throws java.lang.UnsupportedOperationException if the property is not compatible with a float value
	 */
	public void setFloat(Object object, float value)
	{
		for (int i = 0; i < attributes.size() - 1; i++)
			object = attributes.get(i).forceValue(object);
		attributes.get(attributes.size() - 1).setFloat(object, value);
	}

	/**
	 * Reads the property value from the specified object.
	 *
	 * @param object object whose value is to be read
	 *
	 * @return the requested value
	 *
	 * @throws java.lang.UnsupportedOperationException if the property is not compatible with a double value
	 */
	public double getDouble(Object object)
	{
		for (int i = 0; i < attributes.size() && object != null; i++)
			object = attributes.get(i).getValue(object);
		return attributes.get(attributes.size() - 1).getDouble(object);
	}

	/**
	 * Updates the property value of the specified double value.
	 *
	 * @param object object whose value is to be updated
	 * @param value the new value
	 *
	 * @throws java.lang.UnsupportedOperationException if the property is not compatible with a double value
	 */
	public void setDouble(Object object, double value)
	{
		for (int i = 0; i < attributes.size() - 1; i++)
			object = attributes.get(i).forceValue(object);
		attributes.get(attributes.size() - 1).setDouble(object, value);
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
		return lastAttribute.getConstraints();
	}

	public String getColor()
	{
		return lastAttribute.getColor();
	}

	public Icons.Icon getIcon()
	{
		return lastAttribute.getIcon();
	}

	public String getDisplayName()
	{
		return lastAttribute.getDisplayName();
	}

	public String getDescription()
	{
		return lastAttribute.getDescription();
	}

	public String getTooltip()
	{
		return lastAttribute.getTooltip();
	}

	public String getPlaceholder()
	{
		return lastAttribute.getPlaceholder();
	}

	public String getMask()
	{
		return lastAttribute.getMask();
	}

	public Class<?> getRawType()
	{
		return lastAttribute.getRawType();
	}

	public Type getType()
	{
		return lastAttribute.getGenericType();
	}

	public Class<?> getOwner()
	{
		return owner;
	}

	public Type getElementType()
	{
		return lastAttribute.getElementType();
	}

	public Class<?> getElementRawType()
	{
		return lastAttribute.getElementRawType();
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
		return lastAttribute.getConverter();
	}

	public Attribute getLastAttribute()
	{
		return lastAttribute;
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
		return new Property(owner, builder.toString(), attributes.subList(0, attributes.size() - 1));
	}

	public String getColumnName()
	{
		return getAttributes().stream().map(Attribute::getColumnName)
			.filter(Objects::nonNull).collect(Collectors.joining("$"));
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
