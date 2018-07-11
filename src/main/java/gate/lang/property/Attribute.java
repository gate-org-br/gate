package gate.lang.property;

import gate.constraint.Constraint;
import gate.converter.Converter;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public interface Attribute
{

	public Type getType();

	public Type getElementType();

	public Class<?> getRawType();

	public Object getValue(Object object);

	public void setValue(Object object, Object value);

	public Object forceValue(Object object);

	default boolean isEntityId()
	{
		return false;
	}

	default Class<?> getElementRawType()
	{
		Type type = getElementType();
		if (type instanceof Class<?>)
			return (Class<?>) type;
		if (type instanceof ParameterizedType)
			return (Class<?>) ((ParameterizedType) type).getRawType();
		else if (type instanceof GenericArrayType)
			return Array.newInstance((Class<?>) ((ParameterizedType) ((GenericArrayType) type).getGenericComponentType()).getRawType(), 0).getClass();
		else
			return null;
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

			Constructor constructor = Stream.of(type.getConstructors())
				.filter(e -> e.getParameters().length == 0).findAny().orElse(null);
			if (constructor == null)
				throw new PropertyError("No default constructor found in %s.", type.getName());
			constructor.setAccessible(true);
			return constructor.newInstance();
		} catch (InstantiationException
			| IllegalAccessException
			| IllegalArgumentException
			| InvocationTargetException e)
		{
			throw new PropertyError("Error trying to create a instance of %s.", type.getName());
		}

	}

	default Optional<String> getName()
	{
		return Optional.empty();
	}

	default String getDescription()
	{
		return null;
	}

	default String getMask()
	{
		return null;
	}

	default Collection<Constraint.Implementation> getConstraints()
	{
		return Collections.emptyList();
	}

	default String getColumnName()
	{
		return null;
	}

	default String getTableName()
	{
		return Entity.getTableName(getRawType());
	}

	default String getFullTableName()
	{
		return Entity.getFullTableName(getRawType());
	}

	default boolean isEntity()
	{
		return Entity.isEntity(getRawType());
	}

	default Converter getConverter()
	{
		return Converter.getConverter(getRawType());
	}
}
