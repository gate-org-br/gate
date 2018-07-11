package gate.constraint;

import gate.error.AppException;
import gate.lang.property.Property;
import java.util.List;

public class Constraints
{

	public static void validate(Object entity, List<Property> properties) throws AppException
	{
		for (Property property : properties)
			for (Constraint.Implementation constraint : property.getConstraints())
				constraint.validate(entity, property);

	}

	public static void validate(Object entity, String... properties) throws AppException
	{
		validate(entity, Property.getProperties(entity.getClass(), properties));
	}

	public static <T> void validate(Class<T> type, List<T> entities, List<Property> properties) throws AppException
	{
		for (T entity : entities)
			validate(entity, properties);
	}

	public static <T> void validate(Class<T> type, List<T> entities, String... properties) throws AppException
	{
		validate(type, entities, Property.getProperties(type, properties));
	}
}
