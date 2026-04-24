package gate.lang.property;

import gate.annotation.*;
import gate.constraint.Constraint;
import gate.converter.Converter;
import gate.annotation.Entity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Stream;

public abstract class AbstractFieldAttribute implements JavaIdentifierAttribute
{
	private final Type genericType;
	protected final Field field;
	private final Class<?> rawType;
	private final Type elementType;
	private final boolean isEntityId;
	private final Converter converter;
	private final List<Constraint.Implementation<?>> constraints;
	private final Metadata metadata;

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

			var name = Name.Extractor.extract(field).orElse(null);
			var icon = gate.annotation.Icon.Extractor.extract(field).orElse(null);
			var code = Code.Extractor.extract(field).orElse(null);
			var color = Color.Extractor.extract(field).orElse(null);
			var tooltip = Tooltip.Extractor.extract(field).orElse(null);
			var description = Description.Extractor.extract(field).orElseGet(converter::getDescription);
			var mask = Mask.Extractor.extract(field).orElseGet(converter::getMask);
			var placeholder = Placeholder.Extractor.extract(field).orElseGet(converter::getPlaceholder);
			this.metadata = new Metadata(name, description, tooltip, placeholder, mask, color, code, icon);

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

	@Override public Metadata getMetadata() {return metadata;}
	
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
	public boolean isEntityId()
	{
		return isEntityId;
	}

	@Override
	public boolean equals(Object o) {return o instanceof AbstractFieldAttribute a && Objects.equals(field, a.field);}

	@Override public int hashCode()
	{
		return field.hashCode();
	}

	@Override public String toString()
	{
		return field.getName();
	}
}
