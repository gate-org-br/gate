package gate.sql.statement;

import java.util.List;

/**
 * A clause that can be compiled with a set of parameters
 */
public interface Compilable
{

	/**
	 * Compiles the query with a list of parameters.
	 *
	 * @param parameters the list of parameters to be compiled with the query
	 *
	 * @return the same query compiled with the specified parameters
	 */
	SQL parameters(List<Object> parameters);

	/**
	 * Compiles the query with a list of parameters.
	 *
	 * @param parameters the list of parameters to be compiled with the query
	 *
	 * @return the same query compiled with the specified parameters
	 */
	SQL parameters(Object... parameters);

	/**
	 * Marks the query as constant.
	 * <p>
	 * A constant query has no parameters
	 *
	 * @return the same query marked as constant
	 */
	SQL constant();
}
