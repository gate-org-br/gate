package gate.lang.property;

import gate.annotation.Entity;
import gate.constraint.Constraint;
import gate.converter.Converter;
import gate.error.PropertyError;
import gate.util.Reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Stream;

public interface Attribute
{

	Type getGenericType();

	Type getElementType();

	Class<?> getRawType();

	Object getValue(Object object);

	void setValue(Object object, Object value);

	Object forceValue(Object object);

	default boolean isEntityId()
	{
		return false;
	}

	default Class<?> getElementRawType()
	{
		return Reflection.getRawType(getElementType());
	}

	default Object createInstance(Class<?> type)
	{
		try
		{
			if (type == List.class)
				return new ArrayList<>();
			else if (type == Collection.class)
				return new ArrayList<>();
			else if (type == Set.class)
				return new HashSet<>();
			else if (type == Map.class)
				return new HashMap<>();

			Constructor<?> constructor = Stream.of(type.getConstructors())
					.filter(e -> e.getParameters().length == 0).findAny().orElse(null);
			if (constructor == null)
				throw new PropertyError("No default constructor found in %s.", type.getName());
			constructor.setAccessible(true);
			return constructor.newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				 | InvocationTargetException e)
		{
			throw new PropertyError("Error trying to create a instance of %s.", type.getName());
		}

	}

	default Metadata getMetadata() {return Metadata.EMPTY;}

	default boolean getBoolean(Object object)
	{
		throw new UnsupportedOperationException(
				"Attempt to read a boolean value from a non boolean attribute");
	}

	default void setBoolean(Object object, boolean value)
	{
		throw new UnsupportedOperationException(
				"Attempt to write a boolean value to a non boolean attribute");
	}

	default char getChar(Object object)
	{
		throw new UnsupportedOperationException(
				"Attempt to read a char value from a non char attribute");
	}

	default void setChar(Object object, char value)
	{
		throw new UnsupportedOperationException(
				"Attempt to write a char value to a non char attribute");
	}

	default byte getByte(Object object)
	{
		throw new UnsupportedOperationException(
				"Attempt to read a byte value from a non byte attribute");
	}

	default void setByte(Object object, byte value)
	{
		throw new UnsupportedOperationException(
				"Attempt to write a byte value to a non byte attribute");
	}

	default short getShort(Object object)
	{
		throw new UnsupportedOperationException(
				"Attempt to read a short value from a non short attribute");
	}

	default void setShort(Object object, short value)
	{
		throw new UnsupportedOperationException(
				"Attempt to write a short value to a non short attribute");
	}

	default int getInt(Object object)
	{
		throw new UnsupportedOperationException(
				"Attempt to read an int value from a non int attribute");
	}

	default void setInt(Object object, int value)
	{
		throw new UnsupportedOperationException(
				"Attempt to write an int value to a non int attribute");
	}

	default long getLong(Object object)
	{
		throw new UnsupportedOperationException(
				"Attempt to read a long value from a non long attribute");
	}

	default void setLong(Object object, long value)
	{
		throw new UnsupportedOperationException(
				"Attempt to write a long value to a non long attribute");
	}

	default float getFloat(Object object)
	{
		throw new UnsupportedOperationException(
				"Attempt to read a float value from a non float attribute");
	}

	default void setFloat(Object object, float value)
	{
		throw new UnsupportedOperationException(
				"Attempt to write a float value to a non float attribute");
	}

	default double getDouble(Object object)
	{
		throw new UnsupportedOperationException(
				"Attempt to read a double value from a non double attribute");
	}

	default void setDouble(Object object, double value)
	{
		throw new UnsupportedOperationException(
				"Attempt to write a double value to a non double attribute");
	}

	default Collection<Constraint.Implementation<?>> getConstraints()
	{
		return Collections.emptyList();
	}

	default String getColumnName()
	{
		return null;
	}

	default boolean isEntity()
	{
		return getRawType().isAnnotationPresent(Entity.class);
	}

	default Converter getConverter()
	{
		return Converter.getConverter(getRawType());
	}

	default boolean matches(Parameter parameter)
	{
		return toString().equals(parameter.getName())
			   && parameter.getType().isAssignableFrom(getRawType());
	}
}