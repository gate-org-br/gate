package gate.sql.update;

import gate.lang.property.Property;
import gate.sql.GQN;
import gate.sql.statement.Operation;

/**
 * SQL update sentence builder.
 *
 * @author Davi Nunes da Silva
 */
public interface Update
{

	/**
	 * Creates a new SQL update sentence builder for a table.
	 *
	 * @param name name of the table to update
	 *
	 * @return the new update sentence builder created
	 */
	public static TableUpdate table(String name)
	{
		return new TableUpdate(name);
	}

	/**
	 * Creates a new SQL update sentence builder for a java type.
	 *
	 * @param <T> java type to be updated
	 * @param type the java type to be updated
	 *
	 * @return the new update sentence builder created
	 */
	public static <T> TypedUpdate<T> type(Class<T> type)
	{
		return new TypedUpdate<>(type);
	}

	/**
	 * Creates an update sentence from GQN notation.
	 *
	 * @param <T> the type of the entity to be updated
	 * @param type the type of the entity to be updated
	 * @param notation GQN notation to be used to generate the sentence
	 *
	 * @return a new sentence based on the specified type and the specified GQN notation
	 *
	 * @throws gate.error.PropertyError if specified type is not an entity
	 * @throws gate.error.NoSuchPropertyError if any of the specified properties is invalid
	 */
	public static <T> Operation.Builder<T> of(Class<T> type, String... notation)
	{
		GQN<T> GQN = new GQN<>(type, notation);
		return Update.type(type).set(GQN.getProperties()).where(GQN.getCondition(Property::getColumnName));
	}
}
