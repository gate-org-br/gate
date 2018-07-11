package gate.lang.property;

import gate.error.AppError;
import gate.annotation.Column;
import gate.annotation.Description;
import gate.annotation.ElementType;
import gate.annotation.Entity;
import gate.annotation.Mask;
import gate.annotation.Name;
import gate.constraint.Constraint;
import gate.converter.Converter;
import gate.util.Generics;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

class FieldAttribute implements JavaIdentifierAttribute
{

	private final Field field;

	FieldAttribute(Field field)
	{
		this.field = field;
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
	public Type getElementType()
	{
		if (field.getType().isAnnotationPresent(ElementType.class))
			return field.getType().getAnnotation(ElementType.class).value();
		else if (field.getType().isArray())
			return field.getType().getComponentType();
		else if (List.class.isAssignableFrom(field.getType())
			&& field.getGenericType() instanceof ParameterizedType)
			return ((ParameterizedType) field.getGenericType())
				.getActualTypeArguments()[0];
		else if (Map.class.isAssignableFrom(field.getType())
			&& field.getGenericType() instanceof ParameterizedType)
			return ((ParameterizedType) field.getGenericType())
				.getActualTypeArguments()[1];
		return Object.class;
	}

	@Override
	public Object getValue(Object object)
	{
		try
		{
			Method getter = Generics.findGetter(field)
				.orElseThrow(() -> new UnsupportedOperationException(String
				.format("The property %s of class %s does not support reading",
					field.getName(), field.getDeclaringClass().getName())));
			return getter.invoke(object);
		} catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException ex)
		{
			throw new AppError(ex);
		}
	}

	@Override
	public void setValue(Object object, Object value)
	{
		try
		{
			Method setter = Generics.findSetter(field)
				.orElseThrow(() -> new UnsupportedOperationException(String
				.format("The property %s of class %s does not support writing",
					field.getName(), field.getDeclaringClass().getName())));
			setter.invoke(object, value);
		} catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException ex)
		{
			throw new AppError(ex);
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
	public Collection<Constraint.Implementation> getConstraints()
	{
		List<Constraint.Implementation> constraints = new LinkedList<>();
		for (Annotation annotation : field.getAnnotations())
			if (annotation.annotationType().isAnnotationPresent(
				Constraint.class))
				constraints.add(Constraint.Implementation.getImplementation(annotation));
		return constraints;
	}

	@Override
	public String getColumnName()
	{
		if (field.isAnnotationPresent(Column.class))
			return field.getAnnotation(Column.class).value();
		else if (field.getType().isAnnotationPresent(Entity.class))
		{
			char[] chars = field.getName().toCharArray();
			chars[0] = Character.toUpperCase(chars[0]);
			return new String(chars);
		} else
			return field.getName();
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

	@Override
	public Converter getConverter()
	{
		if (field.isAnnotationPresent(gate.annotation.Converter.class))
			try
			{
				return field.getAnnotation(gate.annotation.Converter.class).value().newInstance();
			} catch (InstantiationException | IllegalAccessException e)
			{
				throw new AppError(e);
			}
		return Converter.getConverter(getRawType());
	}

	@Override
	public boolean isEntityId()
	{
		return field.getDeclaringClass().isAnnotationPresent(Entity.class)
			&& field.getName().equals(field.getDeclaringClass().getAnnotation(Entity.class).value());
	}

}
