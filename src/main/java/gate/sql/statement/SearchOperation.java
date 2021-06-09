package gate.sql.statement;

import java.util.Arrays;
import java.util.List;

/**
 * Interface used to define the source of fetching operations.
 *
 * 
 */
public interface SearchOperation<T>
{

	/**
	 * Defines the properties and selection criteria using GQN notation.
	 *
	 * @param GQN properties and selection criteria as GQN notation
	 *
	 * @return a Matcher object to be used to define the query parameters
	 */
	Matcher<T> properties(String... GQN);

	/**
	 * Interface used to define the parameters of fetching operations.
	 *
	 * 
	 */
	interface Matcher<T>
	{

		/**
		 * Defines the object to be used as a filter for query execution.
		 *
		 * @param filter object to be used as a filter for query execution
		 *
		 * @return the results of the query fetched a an object of the specified type
		 */
		List<T> matching(T filter);

		/**
		 * Defines the parameters to be used for query execution.
		 *
		 * @param parameters to be used query execution
		 *
		 * @return the results of the query fetched a an object of the specified type
		 */
		List<T> parameters(List<Object> parameters);

		/**
		 * Defines the parameters to be used for query execution.
		 *
		 * @param parameters to be used query execution
		 *
		 * @return the results of the query fetched a an object of the specified type
		 */
		default List<T> parameters(Object... parameters)
		{
			return parameters(Arrays.asList(parameters));
		}
	}
}
