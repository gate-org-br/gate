package gate.lang.property;

import gate.annotation.*;
import gate.constraint.Constraint;
import gate.converter.Converter;
import gate.icon.Icon;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Stream;

public abstract class AbstractFieldAttribute implements JavaIdentifierAttribute
{
	private final Type genericType;
	private final Icon icon;
	private final String color;
	protected final Field field;
	private final String mask;
	private final String name;
	private final Class<?> rawType;
	private final Type elementType;
	private final String columnName;
	private final String description;
	private final String tooltip;
	private final String code;
	private final String placeholder;
	private final boolean isEntityId;
	private final Converter converter;
	private final List<Constraint.Implementation<?>> constraints;

	AbstractFieldAttribute(Field field)
	{
		try
		{
			this.field = field;
			field.setAccessible(true);

			rawType = field.getType();
			genericType = field.getGenericType();

			isEntityId
					= field.getDeclaringClass().isAnnotationPresent(Entity.class) && field.getName()
					.equals(field.getDeclaringClass().getAnnotation(Entity.class).value());

			if (rawType.isAnnotationPresent(ElementType.class))
				elementType = rawType.getAnnotation(ElementType.class).value();
			else if (rawType.isArray())
				elementType = rawType.getComponentType();
			else if (List.class.isAssignableFrom(rawType)
					&& genericType instanceof ParameterizedType)
				elementType = ((ParameterizedType) genericType).getActualTypeArguments()[0];
			else if (Set.class.isAssignableFrom(rawType)
					&& genericType instanceof ParameterizedType)
				elementType = ((ParameterizedType) genericType).getActualTypeArguments()[0];
			else if (Map.class.isAssignableFrom(rawType)
					&& genericType instanceof ParameterizedType)
				elementType = ((ParameterizedType) genericType).getActualTypeArguments()[1];
			else
				elementType = Object.class;

			converter = field.isAnnotationPresent(gate.annotation.Converter.class)
					? field.getAnnotation(gate.annotation.Converter.class).value()
					.getDeclaredConstructor().newInstance()
					: Converter.getConverter(rawType);

			List<Constraint.Implementation<?>> cons = new ArrayList<>();
			Stream.of(field.getAnnotations()).filter(
							annotation -> annotation.annotationType().isAnnotationPresent(Constraint.class))
					.map(Constraint.Implementation::getImplementation).forEach(cons::add);
			converter.getConstraints().stream()
					.filter(e -> cons.stream().noneMatch(c -> c.getName().equals(e.getName())))
					.forEach(cons::add);
			constraints = Collections.unmodifiableList(cons);

			name = Name.Extractor.extract(field).orElse(null);
			icon = gate.annotation.Icon.Extractor.extract(field).orElse(null);
			code = Code.Extractor.extract(field).orElse(null);
			color = Color.Extractor.extract(field).orElse(null);
			tooltip = Tooltip.Extractor.extract(field).orElse(null);
			description = Description.Extractor.extract(field).orElseGet(converter::getDescription);
			mask = Mask.Extractor.extract(field).orElseGet(converter::getMask);
			placeholder = Placeholder.Extractor.extract(field).orElseGet(converter::getPlaceholder);

			if (field.isAnnotationPresent(Column.class))
				columnName = field.getAnnotation(Column.class).value();
			else if (rawType.isAnnotationPresent(Entity.class))
			{
				char[] chars = field.getName().toCharArray();
				chars[0] = Character.toUpperCase(chars[0]);
				columnName = new String(chars);
			} else
				columnName = field.getName();
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException
				 | InvocationTargetException ex)
		{
			throw new IllegalStateException("Failed to access field attribute", ex);
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
	public Collection<Constraint.Implementation<?>> getConstraints()
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
	public boolean equals(Object obj)
	{
		return obj instanceof AbstractFieldAttribute attribute
				&& Objects.equals(field, attribute.field);
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
