package gate.sql.replace;

import gate.sql.GQN;
import gate.sql.statement.Operation;

/**
 * SQL replace sentence builder.
 *
 * @author Davi Nunes da Silva
 */
public interface Replace
{

	/**
	 * Creates a new SQL replace sentence builder for a table.
	 *
	 * @param table name of the table where to replace
	 *
	 * @return the new replace sentence builder created
	 */
	static TableReplace into(String table)
	{
		return new TableReplace(table);
	}

	/**
	 * Creates a new SQL replace sentence builder for a java type.
	 *
	 * 
	 * @param type the java type to be persisted
	 *
	 * @return the new replace sentence builder created
	 */
	static <T> TypedReplace<T> into(Class<T> type)
	{
		return new TypedReplace<>(type);
	}

	/**
	 * Creates an replace sentence from GQN notation.
	 *
	 * 
	 * @param type the type of the entity to be replaced
	 * @param notation GQN notation to be used to generate the sentence
	 *
	 * @return a new sentence based on the specified type and the specified GQN notation
	 *
	 * @throws gate.error.PropertyError if specified type is not an entity
	 * @throws gate.error.NoSuchPropertyError if any of the specified properties is invalid
	 */
	static <T> Operation<T> of(Class<T> type, String... notation)
	{
		return Replace.into(type).set(new GQN<>(type, notation).getProperties()).build();
	}
}
