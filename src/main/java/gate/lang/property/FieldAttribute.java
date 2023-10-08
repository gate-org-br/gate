package gate.lang.property;

import gate.annotation.Code;
import gate.annotation.Color;
import gate.annotation.Column;
import gate.annotation.Description;
import gate.annotation.ElementType;
import gate.annotation.Entity;
import gate.annotation.Mask;
import gate.annotation.Name;
import gate.annotation.Placeholder;
import gate.annotation.Tooltip;
import gate.constraint.Constraint;
import gate.converter.Converter;
import gate.icon.Icon;
import gate.util.Reflection;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

class FieldAttribute implements JavaIdentifierAttribute
{

	private final Type genericType;
	private final Icon icon;
	private final String color;
	private final Field field;
	private final String mask;
	private final String name;
	private final Method getter;
	private final Method setter;
	private final Class<?> rawType;
	private final Type elementType;
	private final String columnName;
	private final String description;
	private final String tooltip;
	private final String code;
	private final String placeholder;
	private final boolean isEntityId;
	private final Converter converter;
	private final List<Constraint.Implementation> constraints;

	FieldAttribute(Field field)
	{
		try
		{
			this.field = field;
			field.setAccessible(true);

			rawType = field.getType();
			genericType = field.getGenericType();
			getter = Reflection.findGetter(field).orElse(null);
			setter = Reflection.findSetter(field).orElse(null);

			isEntityId = field.getDeclaringClass().isAnnotationPresent(Entity.class)
				&& field.getName().equals(field.getDeclaringClass().getAnnotation(Entity.class).value());

			if (rawType.isAnnotationPresent(ElementType.class))
				elementType = rawType.getAnnotation(ElementType.class).value();
			else if (rawType.isArray())
				elementType = rawType.getComponentType();
			else if (List.class.isAssignableFrom(rawType)
				&& genericType instanceof ParameterizedType)
				elementType = ((ParameterizedType) genericType)
					.getActualTypeArguments()[0];
			else if (Set.class.isAssignableFrom(rawType)
				&& genericType instanceof ParameterizedType)
				elementType = ((ParameterizedType) genericType)
					.getActualTypeArguments()[0];
			else if (Map.class.isAssignableFrom(rawType)
				&& genericType instanceof ParameterizedType)
				elementType = ((ParameterizedType) genericType)
					.getActualTypeArguments()[1];
			else
				elementType = Object.class;

			converter = field.isAnnotationPresent(gate.annotation.Converter.class)
				? field.getAnnotation(gate.annotation.Converter.class).value().getDeclaredConstructor().newInstance()
				: Converter.getConverter(rawType);

			List<Constraint.Implementation> cons = new ArrayList<>();
			Stream.of(field.getAnnotations())
				.filter(annotation -> annotation.annotationType().isAnnotationPresent(Constraint.class))
				.map(Constraint.Implementation::getImplementation)
				.forEach(cons::add);
			converter.getConstraints().stream().filter(e -> cons.stream()
				.noneMatch(c -> c.getName().equals(e.getName()))).forEach(cons::add);
			constraints = Collections.unmodifiableList(cons);

			name = Name.Extractor.extract(field).orElse(null);
			icon = gate.annotation.Icon.Extractor.extract(field).orElse(null);
			code = Code.Extractor.extract(field).orElse(null);
			color = Color.Extractor.extract(field).orElse(null);
			tooltip = Tooltip.Extractor.extract(field).orElse(null);
			description = Description.Extractor.extract(field).orElse(converter.getDescription());

			mask = field.isAnnotationPresent(Mask.class)
				? field.getAnnotation(Mask.class).value()
				: converter.getMask();

			placeholder = field.isAnnotationPresent(Placeholder.class)
				? field.getAnnotation(Placeholder.class).value()
				: converter.getPlaceholder();

			if (field.isAnnotationPresent(Column.class))
				columnName = field.getAnnotation(Column.class).value();
			else if (rawType.isAnnotationPresent(Entity.class))
			{
				char[] chars = field.getName().toCharArray();
				chars[0] = Character.toUpperCase(chars[0]);
				columnName = new String(chars);
			} else
				columnName = field.getName();

		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException ex)
		{
			throw new UnsupportedOperationException(ex);
		}
	}

	@Override
	public Type getGenericType()
	{
		return genericType;
	}

	@Override
	public Class<?> getRawType()
	{
		return rawType;
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
	public String getColor()
	{
		return color;
	}

	@Override
	public Icon getIcon()
	{
		return icon;
	}

	@Override
	public String getDisplayName()
	{
		return name;
	}

	@Override
	public String getMask()
	{
		return mask;
	}

	@Override
	public String getCode()
	{
		return code;
	}

	@Override
	public String getTooltip()
	{
		return tooltip;
	}

	@Override
	public String getDescription()
	{
		return description;
	}

	@Override
	public String getPlaceholder()
	{
		return placeholder;
	}

	@Override
	public String getColumnName()
	{
		return columnName;
	}

	@Override
	public boolean isEntityId()
	{
		return isEntityId;
	}

	@Override
	public Object getValue(Object object)
	{
		try
		{
			if (getter != null)
				return getter.invoke(object);

			if (Modifier.isPublic(field.getModifiers()))
				return field.get(object);

			throw new UnsupportedOperationException(String
				.format("The property %s of class %s does not support reading",
					field.getName(), field.getDeclaringClass().getName()));

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
			if (setter != null)
				setter.invoke(object, value);
			else if (Modifier.isPublic(field.getModifiers())
				&& !Modifier.isFinal(field.getModifiers()))
				field.set(object, value);
			else
				throw new UnsupportedOperationException(String
					.format("The property %s of class %s does not support writing",
						field.getName(), field.getDeclaringClass().getName()));

		} catch (InvocationTargetException | IllegalAccessException ex)
		{
			throw new UnsupportedOperationException(ex);
		}
	}

	@Override
	public Object forceValue(Object object)
	{
		try
		{
			Object value = getValue(object);
			if (value == null)
				field.set(object, value = createInstance(getRawType()));
			return value;
		} catch (IllegalAccessException ex)
		{
			throw new UnsupportedOperationException(ex);
		}
	}

	@Override
	public boolean getBoolean(Object object)
	{
		if (field.getType() != boolean.class)
			return JavaIdentifierAttribute.super.getBoolean(object);

		try
		{
			if (getter != null)
				return (boolean) getter.invoke(object);

			if (Modifier.isPublic(field.getModifiers()))
				return field.getBoolean(object);

			throw new UnsupportedOperationException(String
				.format("The property %s of class %s does not support reading",
					field.getName(), field.getDeclaringClass().getName()));

		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException ex)
		{
			throw new UnsupportedOperationException(ex.getMessage(), ex);
		}
	}

	@Override
	public void setBoolean(Object object, boolean value)
	{
		if (field.getType() != boolean.class)
			JavaIdentifierAttribute.super.setBoolean(object, value);

		try
		{
			if (setter != null)
				setter.invoke(object, value);
			else if (Modifier.isPublic(field.getModifiers())
				&& !Modifier.isFinal(field.getModifiers()))
				field.setBoolean(object, value);
			else
				throw new UnsupportedOperationException(String
					.format("The property %s of class %s does not support writing",
						field.getName(), field.getDeclaringClass().getName()));

		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException ex)
		{
			throw new UnsupportedOperationException(ex.getMessage(), ex);
		}
	}

	@Override
	public char getChar(Object object)
	{
		if (field.getType() != char.class)
			JavaIdentifierAttribute.super.getChar(object);

		try
		{
			if (getter != null)
				return (char) getter.invoke(object);

			if (Modifier.isPublic(field.getModifiers()))
				return field.getChar(object);

			throw new UnsupportedOperationException(String
				.format("The property %s of class %s does not support reading",
					field.getName(), field.getDeclaringClass().getName()));
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException ex)
		{
			throw new UnsupportedOperationException(ex.getMessage(), ex);
		}

	}

	@Override
	public void setChar(Object object, char value)
	{
		if (field.getType() != char.class)
			JavaIdentifierAttribute.super.setChar(object, value);

		try
		{
			if (setter != null)
				setter.invoke(object, value);
			else if (Modifier.isPublic(field.getModifiers())
				&& !Modifier.isFinal(field.getModifiers()))
				field.setChar(object, value);
			else
				throw new UnsupportedOperationException(String
					.format("The property %s of class %s does not support writing",
						field.getName(), field.getDeclaringClass().getName()));

		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException ex)
		{
			throw new UnsupportedOperationException(ex.getMessage(), ex);
		}
	}

	@Override
	public byte getByte(Object object)
	{
		if (field.getType() != byte.class)
			JavaIdentifierAttribute.super.getChar(object);

		try
		{
			if (getter != null)
				return (byte) getter.invoke(object);

			if (Modifier.isPublic(field.getModifiers()))
				return field.getByte(object);

			throw new UnsupportedOperationException(String
				.format("The property %s of class %s does not support reading",
					field.getName(), field.getDeclaringClass().getName()));
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException ex)
		{
			throw new UnsupportedOperationException(ex.getMessage(), ex);
		}

	}

	@Override
	public void setByte(Object object, byte value)
	{
		if (field.getType() != byte.class)
			JavaIdentifierAttribute.super.setByte(object, value);

		try
		{
			if (setter != null)
				setter.invoke(object, value);
			else if (Modifier.isPublic(field.getModifiers())
				&& !Modifier.isFinal(field.getModifiers()))
				field.setByte(object, value);
			else
				throw new UnsupportedOperationException(String
					.format("The property %s of class %s does not support writing",
						field.getName(), field.getDeclaringClass().getName()));

		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException ex)
		{
			throw new UnsupportedOperationException(ex.getMessage(), ex);
		}
	}

	@Override
	public short getShort(Object object)
	{
		if (field.getType() != short.class)
			JavaIdentifierAttribute.super.getShort(object);

		try
		{
			if (getter != null)
				return (short) getter.invoke(object);

			if (Modifier.isPublic(field.getModifiers()))
				return field.getShort(object);

			throw new UnsupportedOperationException(String
				.format("The property %s of class %s does not support reading",
					field.getName(), field.getDeclaringClass().getName()));
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException ex)
		{
			throw new UnsupportedOperationException(ex.getMessage(), ex);
		}

	}

	@Override
	public void setShort(Object object, short value)
	{
		if (field.getType() != short.class)
			JavaIdentifierAttribute.super.setShort(object, value);

		try
		{
			if (setter != null)
				setter.invoke(object, value);
			else if (Modifier.isPublic(field.getModifiers())
				&& !Modifier.isFinal(field.getModifiers()))
				field.setShort(object, value);
			else
				throw new UnsupportedOperationException(String
					.format("The property %s of class %s does not support writing",
						field.getName(), field.getDeclaringClass().getName()));

		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException ex)
		{
			throw new UnsupportedOperationException(ex.getMessage(), ex);
		}
	}

	@Override
	public int getInt(Object object)
	{
		if (field.getType() != int.class)
			JavaIdentifierAttribute.super.getInt(object);

		try
		{
			if (getter != null)
				return (int) getter.invoke(object);

			if (Modifier.isPublic(field.getModifiers()))
				return field.getInt(object);

			throw new UnsupportedOperationException(String
				.format("The property %s of class %s does not support reading",
					field.getName(), field.getDeclaringClass().getName()));
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException ex)
		{
			throw new UnsupportedOperationException(ex.getMessage(), ex);
		}
	}

	@Override
	public void setInt(Object object, int value)
	{
		if (field.getType() != int.class)
			JavaIdentifierAttribute.super.setInt(object, value);

		try
		{
			if (setter != null)
				setter.invoke(object, value);
			else if (Modifier.isPublic(field.getModifiers())
				&& !Modifier.isFinal(field.getModifiers()))
				field.setInt(object, value);
			else
				throw new UnsupportedOperationException(String
					.format("The property %s of class %s does not support writing",
						field.getName(), field.getDeclaringClass().getName()));

		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException ex)
		{
			throw new UnsupportedOperationException(ex.getMessage(), ex);
		}
	}

	@Override
	public long getLong(Object object)
	{
		if (field.getType() != long.class)
			JavaIdentifierAttribute.super.getLong(object);

		try
		{
			if (getter != null)
				return (long) getter.invoke(object);

			if (Modifier.isPublic(field.getModifiers()))
				return field.getLong(object);

			throw new UnsupportedOperationException(String
				.format("The property %s of class %s does not support reading",
					field.getName(), field.getDeclaringClass().getName()));
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException ex)
		{
			throw new UnsupportedOperationException(ex.getMessage(), ex);
		}
	}

	@Override
	public void setLong(Object object, long value)
	{
		if (field.getType() != long.class)
			JavaIdentifierAttribute.super.setLong(object, value);

		try
		{
			if (setter != null)
				setter.invoke(object, value);
			else if (Modifier.isPublic(field.getModifiers())
				&& !Modifier.isFinal(field.getModifiers()))
				field.setLong(object, value);
			else
				throw new UnsupportedOperationException(String
					.format("The property %s of class %s does not support writing",
						field.getName(), field.getDeclaringClass().getName()));

		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException ex)
		{
			throw new UnsupportedOperationException(ex.getMessage(), ex);
		}
	}

	@Override
	public float getFloat(Object object)
	{
		if (field.getType() != float.class)
			JavaIdentifierAttribute.super.getFloat(object);

		try
		{
			if (getter != null)
				return (float) getter.invoke(object);

			if (Modifier.isPublic(field.getModifiers()))
				return field.getFloat(object);

			throw new UnsupportedOperationException(String
				.format("The property %s of class %s does not support reading",
					field.getName(), field.getDeclaringClass().getName()));
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException ex)
		{
			throw new UnsupportedOperationException(ex.getMessage(), ex);
		}
	}

	@Override
	public void setFloat(Object object, float value)
	{
		if (field.getType() != float.class)
			JavaIdentifierAttribute.super.setFloat(object, value);

		try
		{
			if (setter != null)
				setter.invoke(object, value);
			else if (Modifier.isPublic(field.getModifiers())
				&& !Modifier.isFinal(field.getModifiers()))
				field.setFloat(object, value);
			else
				throw new UnsupportedOperationException(String
					.format("The property %s of class %s does not support writing",
						field.getName(), field.getDeclaringClass().getName()));

		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException ex)
		{
			throw new UnsupportedOperationException(ex.getMessage(), ex);
		}
	}

	@Override
	public double getDouble(Object object)
	{
		if (field.getType() != double.class)
			JavaIdentifierAttribute.super.getDouble(object);

		try
		{
			if (getter != null)
				return (double) getter.invoke(object);

			if (Modifier.isPublic(field.getModifiers()))
				return field.getDouble(object);

			throw new UnsupportedOperationException(String
				.format("The property %s of class %s does not support reading",
					field.getName(), field.getDeclaringClass().getName()));
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException ex)
		{
			throw new UnsupportedOperationException(ex.getMessage(), ex);
		}
	}

	@Override
	public void setDouble(Object object, double value)
	{
		if (field.getType() != double.class)
			JavaIdentifierAttribute.super.setDouble(object, value);

		try
		{
			if (setter != null)
				setter.invoke(object, value);
			else if (Modifier.isPublic(field.getModifiers())
				&& !Modifier.isFinal(field.getModifiers()))
				field.setDouble(object, value);
			else
				throw new UnsupportedOperationException(String
					.format("The property %s of class %s does not support writing",
						field.getName(), field.getDeclaringClass().getName()));

		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException ex)
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
