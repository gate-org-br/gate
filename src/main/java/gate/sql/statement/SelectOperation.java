package gate.sql.statement;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Interface used to define the source of fetching operations.
 *
 * @param <T> type of the object to be fetched by the fetching operation
 */
public interface SelectOperation<T>
{

	/**
	 * Defines the properties and selection criteria using GQN notation.
	 *
	 * @param GQN properties and selection criteria as GQN notation
	 *
	 * @return a Matcher object to be used to define the query parameters
	 */
	public Matcher<T> properties(String... GQN);

	/**
	 * Interface used to define the parameters of fetching operations.
	 *
	 * @param <T> type of the object to be fetched by the fetching operation
	 */
	public interface Matcher<T>
	{

		/**
		 * Defines the object to be used as a filter for query execution.
		 *
		 * @param filter object to be used as a filter for query execution
		 *
		 * @return an Optional describing the first row of the result as an object of the specified type or an empty
		 *         Optional if the result is empty
		 */
		public Optional<T> matching(T filter);

		/**
		 * Defines the parameters to be used for query execution.
		 *
		 * @param parameters to be used for query execution
		 *
		 * @return an Optional describing the first row of the result as an object of the specified type or an empty
		 *         Optional if the result is empty
		 */
		public Optional<T> parameters(List<Object> parameters);

		/**
		 * Defines the parameters to be used for query execution.
		 *
		 * @param parameters to be used query execution
		 *
		 * @return an Optional describing the first row of the result as an object of the specified type or an empty
		 *         Optional if the result is empty
		 */
		public default Optional<T> parameters(Object... parameters)
		{
			return parameters(Arrays.asList(parameters));
		}
	}
}
