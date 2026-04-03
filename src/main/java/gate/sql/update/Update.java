package gate.sql.update;

import gate.io.StringReader;

import java.net.URL;
import java.util.List;
import java.util.Objects;

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
	 * @return the new update sentence builder created
	 */
	static TableUpdate table(String name)
	{
		return new TableUpdate("update " + name);
	}

	/**
	 * Creates a new SQL update sentence builder for a table.
	 *
	 * @param type java type related with the table to update
	 * @return the new update sentence builder created
	 */
	static <T> ClassUpdate<T> table(Class<T> type)
	{
		return new ClassUpdate<>(type);
	}

	static <T> ObjectUpdate<T> table(T object)
	{
		@SuppressWarnings("unchecked")
		Class<T> type = (Class<T>) Objects.requireNonNull(object).getClass();
		return new ObjectUpdate<>(type, object);
	}

	/**
	 * Creates a new SQL update from the specified SQL.
	 *
	 * @param sql SQL string from where to build the update
	 * @return the new update sentence builder created
	 */
	static TableUpdate of(String sql)
	{
		return new TableUpdate(sql);
	}

	/**
	 * Creates a new SQL update from the specified SQL compiled with the specified parameters.
	 *
	 * @param sql        SQL string from where to build the update
	 * @param parameters list of parameters to be compiled with the update
	 * @return the new update sentence builder created
	 */
	static TableUpdate.Compiled of(String sql, List<Object> parameters)
	{
		return new TableUpdate(sql).new Compiled(parameters);
	}

	/**
	 * Creates a new SQL update from the specified SQL resource file.
	 *
	 * @param resource resource file form where to load the update SQL statement
	 * @return the new update sentence builder created
	 */
	static TableUpdate of(URL resource)
	{
		return of(StringReader.read(resource));
	}

	/**
	 * Creates a new SQL update from the specified SQL resource file compiled with the specified parameters.
	 *
	 * @param resource   resource file form where to load the update SQL statement
	 * @param parameters list of parameters to be compiled with the update
	 * @return the new update sentence builder created
	 */
	static TableUpdate.Compiled of(URL resource, List<Object> parameters)
	{
		return of(StringReader.read(resource), parameters);
	}

}
