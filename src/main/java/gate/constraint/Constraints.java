package gate.constraint;

import gate.error.AppException;
import gate.lang.property.Property;
import java.util.List;

public class Constraints
{

	/**
	 * Validates the specified properties of the specified entity according to it's constraints
	 *
	 * @param entity the entity whose properties must be validated
	 * @param properties the properties to be validated
	 *
	 * @throws AppException if any constraint is violated during validation
	 *
	 * @see gate.constraint.Length
	 * @see gate.constraint.Required
	 * @see gate.constraint.Maxlength
	 * @see gate.constraint.Minlength
	 * @see gate.constraint.Pattern
	 * @see gate.constraint.Step
	 * @see gate.constraint.Min
	 * @see gate.constraint.Max
	 */
	public static void validate(Object entity, List<Property> properties) throws AppException
	{
		for (Property property : properties)
			for (Constraint.Implementation<?> constraint : property.getConstraints())
				constraint.validate(entity, property);

	}

	/**
	 * Validates the specified properties of the specified entity according to it's constraints
	 *
	 * @param entity the entity whose properties must be validated
	 * @param properties the properties to be validated
	 *
	 * @throws AppException if any constraint is violated during validation
	 *
	 * @see gate.constraint.Length
	 * @see gate.constraint.Required
	 * @see gate.constraint.Maxlength
	 * @see gate.constraint.Minlength
	 * @see gate.constraint.Pattern
	 * @see gate.constraint.Step
	 * @see gate.constraint.Min
	 * @see gate.constraint.Max
	 */
	public static void validate(Object entity, String... properties) throws AppException
	{
		validate(entity, Property.getProperties(entity.getClass(), properties));
	}

	/**
	 * Validates the specified properties of the specified entities according to it's constraints
	 *
	 * 
	 * @param type type of the entities to be validated
	 * @param entities the entities whose properties must be validated
	 * @param properties the properties to be validated
	 *
	 * @throws AppException if any constraint is violated during validation
	 *
	 * @see gate.constraint.Length
	 * @see gate.constraint.Required
	 * @see gate.constraint.Maxlength
	 * @see gate.constraint.Minlength
	 * @see gate.constraint.Pattern
	 * @see gate.constraint.Step
	 * @see gate.constraint.Min
	 * @see gate.constraint.Max
	 */
	public static <T> void validate(Class<T> type, List<T> entities, List<Property> properties) throws AppException
	{
		for (T entity : entities)
			validate(entity, properties);
	}

	/**
	 * Validates the specified properties of the specified entities according to it's constraints
	 *
	 * 
	 * @param type type of the entities to be validated
	 * @param entities the entities whose properties must be validated
	 * @param properties the properties to be validated
	 *
	 * @throws AppException if any constraint is violated during validation
	 *
	 * @see gate.constraint.Length
	 * @see gate.constraint.Required
	 * @see gate.constraint.Maxlength
	 * @see gate.constraint.Minlength
	 * @see gate.constraint.Pattern
	 * @see gate.constraint.Step
	 * @see gate.constraint.Min
	 * @see gate.constraint.Max
	 */
	public static <T> void validate(Class<T> type, List<T> entities, String... properties) throws AppException
	{
		validate(type, entities, Property.getProperties(type, properties));
	}
}
