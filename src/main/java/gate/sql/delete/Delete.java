package gate.sql.delete;

import gate.lang.property.Property;
import gate.sql.GQN;
import gate.sql.statement.Operation;

/**
 * SQL delete sentence builder.
 *
 * @author Davi Nunes da Silva
 */
public interface Delete
{

	/**
	 * Creates a new SQL delete sentence builder for a table.
	 *
	 * @param table name of the table to delete
	 *
	 * @return the new delete sentence builder created
	 */
	static TableDelete from(String table)
	{
		return new TableDelete(table);
	}

	/**
	 * Creates a new SQL delete sentence builder for a java type.
	 *
	 * 
	 * @param type the java type to be deleted
	 *
	 * @return the new deleted sentence builder created
	 */
	static <T> TypedDelete<T> from(Class<T> type)
	{
		return new TypedDelete<>(type);
	}

	/**
	 * Creates an delete sentence from GQN notation.
	 *
	 * 
	 * @param type the type of the entity to be deleted
	 * @param notation GQN notation to be used to generate the sentence
	 *
	 * @return a new sentence based on the specified type and the specified GQN notation
	 *
	 * @throws gate.error.PropertyError if specified type is not an entity
	 * @throws gate.error.NoSuchPropertyError if any of the specified properties is invalid
	 */
	static <T> Operation<T> of(Class<T> type, String... notation)
	{
		return Delete.from(type).where(new GQN<>(type, notation).getCondition(Property::getColumnName)).build();
	}
}
