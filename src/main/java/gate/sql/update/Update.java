package gate.sql.update;

import gate.lang.property.Property;
import gate.sql.GQN;
import gate.sql.statement.Operation;
import gate.util.Resources;
import java.net.URL;
import java.util.List;

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
	static TableUpdate table(String name)
	{
		return new TableUpdate("update " + name);
	}

	/**
	 * Creates a new SQL update from the specified sql.
	 *
	 * @param sql sql string from where to build the update
	 *
	 * @return the new update sentence builder created
	 */
	static TableUpdate of(String sql)
	{
		return new TableUpdate(sql);
	}

	/**
	 * Creates a new SQL update from the specified sql compiled with the specified parameters.
	 *
	 * @param sql sql string from where to build the update
	 * @param parameters list of parameters to be compiled with the update
	 *
	 * @return the new update sentence builder created
	 */
	static TableUpdate.Compiled of(String sql, List<Object> parameters)
	{
		return new TableUpdate(sql).new Compiled(parameters);
	}

	/**
	 * Creates a new SQL update from the specified sql resource file.
	 *
	 * @param resource resource file form where to load the update sql statement
	 *
	 * @return the new update sentence builder created
	 */
	public static TableUpdate of(URL resource)
	{
		return of(Resources.getTextResource(resource));
	}

	/**
	 * Creates a new SQL update from the specified sql resource file compiled with the specified parameters..
	 *
	 * @param resource resource file form where to load the update sql statement
	 * @param parameters list of parameters to be compiled with the update
	 *
	 * @return the new update sentence builder created
	 */
	public static TableUpdate.Compiled of(URL resource, List<Object> parameters)
	{
		return of(Resources.getTextResource(resource), parameters);
	}

	/**
	 * Creates a new SQL update sentence builder for a java type.
	 *
	 * 
	 * @param type the java type to be updated
	 *
	 * @return the new update sentence builder created
	 */
	static <T> TypedUpdate<T> type(Class<T> type)
	{
		return new TypedUpdate<>(type);
	}

	/**
	 * Creates an update sentence from GQN notation.
	 *
	 * 
	 * @param type the type of the entity to be updated
	 * @param notation GQN notation to be used to generate the sentence
	 *
	 * @return a new sentence based on the specified type and the specified GQN notation
	 *
	 * @throws gate.error.PropertyError if specified type is not an entity
	 * @throws gate.error.NoSuchPropertyError if any of the specified properties is invalid
	 */
	static <T> Operation.Builder<T> of(Class<T> type, String... notation)
	{
		GQN<T> GQN = new GQN<>(type, notation);
		return Update.type(type).set(GQN.getProperties()).where(GQN.getCondition(Property::getColumnName));
	}
}
