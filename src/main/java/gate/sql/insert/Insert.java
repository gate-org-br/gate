package gate.sql.insert;

import gate.sql.GQN;
import gate.sql.statement.Operation;

/**
 * SQL insert sentence builder.
 *
 * @author Davi Nunes da Silva
 */
public interface Insert
{

	/**
	 * Creates a new SQL insert sentence builder for a table.
	 *
	 * @param table name of the table where to insert
	 * @return the new insert sentence builder created
	 */
	static TableInsert into(String table)
	{
		return new TableInsert(table);
	}

	/**
	 * Creates a new SQL insert sentence builder for a java type using
	 * {@link gate.type.PropertyReference} accessors.
	 *
	 * @param type the java type to be persisted
	 * @return the new insert sentence builder created
	 */
	static <T> ClassInsert<T> into(Class<T> type) {return new ClassInsert<>(type);}

	/**
	 * @param type the java type to be persisted
	 * @return the new insert sentence builder created
	 */
	static <T> TypedInsert<T> type(Class<T> type)
	{
		return new TypedInsert<>(type);
	}

	/**
	 * Creates an insert sentence from GQN notation.
	 *
	 * @param type     the type of the entity to be inserted
	 * @param notation GQN notation to be used to generate the sentence
	 * @return a new sentence based on the specified type and the specified GQN notation
	 * @throws gate.error.PropertyError       if specified type is not an entity
	 * @throws gate.error.NoSuchPropertyError if any of the specified properties is invalid
	 */
	static <T> Operation<T> of(Class<T> type, String... notation)
	{
		return Insert.type(type).set(new GQN<>(type, notation).getProperties()).build();
	}
}