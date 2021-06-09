package gate.sql.statement;

import gate.error.ConstraintViolationException;
import gate.error.FKViolationException;
import gate.error.UKViolationException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Context to define the properties, the values and the criteria of update operations.
 *
 * 
 */
public interface UpdateOperation<T>
{

	/**
	 * Updates all properties of the defined objects matching the id.
	 *
	 * @param values objects to be updated
	 *
	 * @return the number of records affected by the update operation
	 *
	 * @throws gate.error.ConstraintViolationException if a constraint is violated during execution
	 * @throws gate.error.FKViolationException if a foreign key constraint is violated during execution
	 * @throws gate.error.UKViolationException if a unique key constraint is violated during execution
	 */
	int execute(Collection<T> values) throws ConstraintViolationException,
			FKViolationException,
			UKViolationException;

	/**
	 * Updates all properties of the defined object matching the id.
	 *
	 * @param value object to be updated
	 *
	 * @return the number of records affected by the update operation
	 *
	 * @throws gate.error.ConstraintViolationException if a constraint is violated during execution
	 * @throws gate.error.FKViolationException if a foreign key constraint is violated during execution
	 * @throws gate.error.UKViolationException if a unique key constraint is violated during execution
	 */
	default int execute(T value) throws ConstraintViolationException,
			FKViolationException,
			UKViolationException
	{
		return execute(Collections.singleton(value));
	}

	/**
	 * Updates all properties of the defined objects matching the id.
	 *
	 * @param values objects to be updated
	 *
	 * @return the number of records affected by the update operation
	 *
	 * @throws gate.error.ConstraintViolationException if a constraint is violated during execution
	 * @throws gate.error.FKViolationException if a foreign key constraint is violated during execution
	 * @throws gate.error.UKViolationException if a unique key constraint is violated during execution
	 */
	default int execute(T... values) throws ConstraintViolationException,
			FKViolationException,
			UKViolationException
	{
		return execute(Arrays.asList(values));
	}

	/**
	 * Define the properties to be updated and used as update criteria.
	 *
	 * @param properties properties to be updated and used as update criteria
	 *
	 * @return a Parameters object to be used to define the objects to be updated
	 */
	Properties<T> properties(String... properties);

	/**
	 * Context to define the values to be updated.
	 *
	 * 
	 */
	interface Properties<T>
	{

		/**
		 * Updates the defined properties of the defined objects matching the defined criteria.
		 *
		 * @param values objects to be updated
		 *
		 * @return the number of records affected by the update operation
		 *
		 * @throws gate.error.ConstraintViolationException if a constraint is violated during execution
		 * @throws gate.error.FKViolationException if a foreign key constraint is violated during execution
		 * @throws gate.error.UKViolationException if a unique key constraint is violated during execution
		 */
		int execute(Collection<T> values) throws ConstraintViolationException,
				FKViolationException,
				UKViolationException;

		/**
		 * Updates the specified properties of the specified object matching the specified criteria.
		 *
		 * @param value object to be updated
		 *
		 * @return the number of records affected by the update operation
		 *
		 * @throws gate.error.ConstraintViolationException if a database constraint is violated during execution
		 * @throws gate.error.FKViolationException if a foreign key constraint is violated during execution
		 * @throws gate.error.UKViolationException if a unique key constraint is violated during execution
		 */
		default int execute(T value) throws ConstraintViolationException,
				FKViolationException,
				UKViolationException
		{
			return execute(Collections.singleton(value));
		}

		/**
		 * Updates the defined properties of the defined objects matching the defined criteria.
		 *
		 * @param values objects to be updated
		 *
		 * @return the number of records affected by the update operation
		 *
		 * @throws gate.error.ConstraintViolationException if a constraint is violated during execution
		 * @throws gate.error.FKViolationException if a foreign key constraint is violated during execution
		 * @throws gate.error.UKViolationException if a unique key constraint is violated during execution
		 */
		default int execute(T... values) throws ConstraintViolationException,
				FKViolationException,
				UKViolationException
		{
			return execute(Arrays.asList(values));
		}
	}
}
