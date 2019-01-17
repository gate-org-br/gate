package gate.lang.property;

import gate.annotation.Column;
import gate.annotation.Description;
import gate.annotation.ElementType;
import gate.annotation.Entity;
import gate.annotation.Mask;
import gate.annotation.Name;
import gate.constraint.Constraint;
import gate.converter.Converter;
import gate.util.Reflection;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class FieldAttribute implements JavaIdentifierAttribute
{

	private final Field field;
	private final Method getter;
	private final Method setter;
	private final Type elementType;
	private final String columnName;
	private final Converter converter;
	private final List<Constraint.Implementation> constraints;

	FieldAttribute(Field field)
	{
		try
		{
			this.field = field;
			field.setAccessible(true);
			getter = Reflection.findGetter(field).orElse(null);
			setter = Reflection.findSetter(field).orElse(null);

			constraints = Collections.unmodifiableList(Stream.of(field.getAnnotations())
				.filter(annotation -> annotation.annotationType().isAnnotationPresent(Constraint.class))
				.map(constraint -> Constraint.Implementation.getImplementation(constraint))
				.collect(Collectors.toList()));

			converter = field.isAnnotationPresent(gate.annotation.Converter.class)
				? field.getAnnotation(gate.annotation.Converter.class).value().newInstance()
				: Converter.getConverter(field.getType());

			if (field.getType().isAnnotationPresent(ElementType.class))
				elementType = field.getType()
					.getAnnotation(ElementType.class).value();
			else if (field.getType().isArray())
				elementType = field.getType().getComponentType();
			else if (List.class.isAssignableFrom(field.getType())
				&& field.getGenericType() instanceof ParameterizedType)
				elementType = ((ParameterizedType) field.getGenericType())
					.getActualTypeArguments()[0];
			else if (Map.class.isAssignableFrom(field.getType())
				&& field.getGenericType() instanceof ParameterizedType)
				elementType = ((ParameterizedType) field.getGenericType())
					.getActualTypeArguments()[1];
			else
				elementType = Object.class;

			if (field.isAnnotationPresent(Column.class))
				columnName = field.getAnnotation(Column.class).value();
			else if (field.getType().isAnnotationPresent(Entity.class))
			{
				char[] chars = field.getName().toCharArray();
				chars[0] = Character.toUpperCase(chars[0]);
				columnName = new String(chars);
			} else
				columnName = field.getName();

		} catch (InstantiationException | IllegalAccessException ex)
		{
			throw new UnsupportedOperationException(ex);
		}
	}

	@Override
	public Type getType()
	{
		return field.getGenericType();
	}

	@Override
	public Class<?> getRawType()
	{
		return field.getType();
	}

	@Override
	public Collection<Constraint.Implementation> getConstraints()
	{
		return constraints;
	}

	@Override
	public Converter getConverter()
	{
		return converter;
	}

	@Override
	public Type getElementType()
	{
		return elementType;
	}

	@Override
	public Optional<String> getName()
	{
		return field.isAnnotationPresent(Name.class)
			? Optional.of(field.getAnnotation(Name.class).value())
			: Optional.empty();
	}

	@Override
	public String getMask()
	{
		return field.isAnnotationPresent(Mask.class)
			? field.getAnnotation(Mask.class).value() : null;
	}

	@Override
	public String getDescription()
	{
		return field.isAnnotationPresent(Description.class)
			? field.getAnnotation(Description.class).value() : null;
	}

	@Override
	public String getColumnName()
	{
		return columnName;
	}

	@Override
	public boolean isEntityId()
	{
		return field.getDeclaringClass().isAnnotationPresent(Entity.class)
			&& field.getName().equals(field.getDeclaringClass().getAnnotation(Entity.class).value());
	}

	@Override
	public Object getValue(Object object)
	{
		try
		{
			if (getter == null)
				throw new UnsupportedOperationException(String
					.format("The property %s of class %s does not support reading",
						field.getName(), field.getDeclaringClass().getName()));
			return getter.invoke(object);
		} catch (InvocationTargetException | IllegalAccessException ex)
		{
			throw new UnsupportedOperationException(ex);
		}
	}

	@Override
	public void setValue(Object object, Object value)
	{
		try
		{
			if (setter == null)
				throw new UnsupportedOperationException(String
					.format("The property %s of class %s does not support writing",
						field.getName(), field.getDeclaringClass().getName()));
			setter.invoke(object, value);
		} catch (InvocationTargetException | IllegalAccessException ex)
		{
			throw new UnsupportedOperationException(ex);
		}
	}

	@Override
	public Object forceValue(Object object)
	{
		Object value = getValue(object);
		if (value == null)
			setValue(object, value = createInstance(getRawType()));
		return value;
	}

	@Override
	public boolean getBoolean(Object object)
	{
		try
		{
			return field.getType() == boolean.class
				? field.getBoolean(object)
				: JavaIdentifierAttribute.super.getBoolean(object);
		} catch (IllegalArgumentException | IllegalAccessException ex)
		{
			throw new UnsupportedOperationException(ex.getMessage(), ex);
		}

	}

	@Override
	public void setBoolean(Object object, boolean value)
	{
		try
		{
			if (field.getType() == boolean.class)
				field.setBoolean(object, value);
			else
				JavaIdentifierAttribute.super.setBoolean(object, value);
		} catch (IllegalArgumentException | IllegalAccessException ex)
		{
			throw new UnsupportedOperationException(ex.getMessage(), ex);
		}
	}

	@Override
	public char getChar(Object object)
	{
		try
		{
			return field.getType() == char.class
				? field.getChar(object)
				: JavaIdentifierAttribute.super.getChar(object);
		} catch (IllegalArgumentException | IllegalAccessException ex)
		{
			throw new UnsupportedOperationException(ex.getMessage(), ex);
		}

	}

	@Override
	public void setChar(Object object, char value)
	{
		try
		{
			if (field.getType() == char.class)
				field.setChar(object, value);
			else
				JavaIdentifierAttribute.super.setChar(object, value);
		} catch (IllegalArgumentException | IllegalAccessException ex)
		{
			throw new UnsupportedOperationException(ex.getMessage(), ex);
		}
	}

	@Override
	public byte getByte(Object object)
	{
		try
		{
			return field.getType() == byte.class
				? field.getByte(object)
				: JavaIdentifierAttribute.super.getByte(object);
		} catch (IllegalArgumentException | IllegalAccessException ex)
		{
			throw new UnsupportedOperationException(ex.getMessage(), ex);
		}

	}

	@Override
	public void setByte(Object object, byte value)
	{
		try
		{
			if (field.getType() == byte.class)
				field.setByte(object, value);
			else
				JavaIdentifierAttribute.super.setByte(object, value);
		} catch (IllegalArgumentException | IllegalAccessException ex)
		{
			throw new UnsupportedOperationException(ex.getMessage(), ex);
		}
	}

	@Override
	public short getShort(Object object)
	{
		try
		{
			return field.getType() == short.class
				? field.getShort(object)
				: JavaIdentifierAttribute.super.getShort(object);
		} catch (IllegalArgumentException | IllegalAccessException ex)
		{
			throw new UnsupportedOperationException(ex.getMessage(), ex);
		}

	}

	@Override
	public void setShort(Object object, short value)
	{
		try
		{
			if (field.getType() == short.class)
				field.setShort(object, value);
			else
				JavaIdentifierAttribute.super.setShort(object, value);
		} catch (IllegalArgumentException | IllegalAccessException ex)
		{
			throw new UnsupportedOperationException(ex.getMessage(), ex);
		}
	}

	@Override
	public int getInt(Object object)
	{
		try
		{
			return field.getType() == int.class
				? field.getInt(object)
				: JavaIdentifierAttribute.super.getInt(object);
		} catch (IllegalArgumentException | IllegalAccessException ex)
		{
			throw new UnsupportedOperationException(ex.getMessage(), ex);
		}
	}

	@Override
	public void setInt(Object object, int value)
	{
		try
		{
			if (field.getType() == int.class)
				field.setInt(object, value);
			else
				JavaIdentifierAttribute.super.setInt(object, value);
		} catch (IllegalArgumentException | IllegalAccessException ex)
		{
			throw new UnsupportedOperationException(ex.getMessage(), ex);
		}
	}

	@Override
	public long getLong(Object object)
	{
		try
		{
			return field.getType() == long.class
				? field.getLong(object)
				: JavaIdentifierAttribute.super.getLong(object);
		} catch (IllegalArgumentException | IllegalAccessException ex)
		{
			throw new UnsupportedOperationException(ex.getMessage(), ex);
		}
	}

	@Override
	public void setLong(Object object, long value)
	{
		try
		{
			if (field.getType() == long.class)
				field.setLong(object, value);
			else
				JavaIdentifierAttribute.super.setLong(object, value);
		} catch (IllegalArgumentException | IllegalAccessException ex)
		{
			throw new UnsupportedOperationException(ex.getMessage(), ex);
		}
	}

	@Override
	public float getFloat(Object object)
	{
		try
		{
			return field.getType() == float.class
				? field.getFloat(object)
				: JavaIdentifierAttribute.super.getFloat(object);
		} catch (IllegalArgumentException | IllegalAccessException ex)
		{
			throw new UnsupportedOperationException(ex.getMessage(), ex);
		}
	}

	@Override
	public void setFloat(Object object, float value)
	{
		try
		{
			if (field.getType() == float.class)
				field.setFloat(object, value);
			else
				JavaIdentifierAttribute.super.setFloat(object, value);
		} catch (IllegalArgumentException | IllegalAccessException ex)
		{
			throw new UnsupportedOperationException(ex.getMessage(), ex);
		}
	}

	@Override
	public double getDouble(Object object)
	{
		try
		{
			return field.getType() == double.class
				? field.getDouble(object)
				: JavaIdentifierAttribute.super.getDouble(object);
		} catch (IllegalArgumentException | IllegalAccessException ex)
		{
			throw new UnsupportedOperationException(ex.getMessage(), ex);
		}
	}

	@Override
	public void setDouble(Object object, double value)
	{
		try
		{
			if (field.getType() == double.class)
				field.setDouble(object, value);
			else
				JavaIdentifierAttribute.super.setDouble(object, value);
		} catch (IllegalArgumentException | IllegalAccessException ex)
		{
			throw new UnsupportedOperationException(ex.getMessage(), ex);
		}
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof FieldAttribute
			&& Objects.equals(field, ((FieldAttribute) obj).field);
	}

	@Override
	public int hashCode()
	{
		return field.hashCode();
	}

	@Override
	public String toString()
	{
		return field.getName();
	}

}
