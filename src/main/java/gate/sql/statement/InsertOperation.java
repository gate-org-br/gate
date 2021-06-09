package gate.sql.statement;

import gate.error.ConstraintViolationException;
import gate.error.FKViolationException;
import gate.error.UKViolationException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;

/**
 * Context to define the properties and the values to be inserted.
 *
 * 
 */
public interface InsertOperation<T>
{

	InsertOperation observe(Consumer<T> observer);

	/**
	 * Inserts the defined objects on the database.
	 *
	 * @param values objects to be inserted on the database
	 *
	 * @return the number of records affected by the insert sentence
	 *
	 * @throws gate.error.ConstraintViolationException if a constraint is violated during execution
	 * @throws gate.error.FKViolationException if a foreign key constraint is violated during execution
	 * @throws gate.error.UKViolationException if a unique key constraint is violated during execution
	 */
	int execute(Collection<T> values) throws ConstraintViolationException,
						 FKViolationException,
						 UKViolationException;

	/**
	 * Inserts the defined object on the database.
	 *
	 * @param value object to be inserted on the database
	 *
	 * @return the number of records affected by the insert sentence
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
	 * Inserts the defined objects on the database.
	 *
	 * @param values objects to be inserted on the database
	 *
	 * @return the number of records affected by the insert sentence
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
	 * Define the properties to be persisted
	 *
	 * @param properties the properties to be persisted
	 *
	 * @return a Parameters object to be used to define the values
	 */
	Properties<T> properties(String... properties);

	/**
	 * Context to define the properties and the values to be inserted.
	 *
	 * 
	 */
	interface Properties<T>
	{

		InsertOperation.Properties observe(Consumer<T> observer);

		/**
		 * Inserts the defined objects on the database.
		 *
		 * @param values objects to be inserted on the database
		 *
		 * @return the number of records affected by the insert sentence
		 *
		 * @throws gate.error.ConstraintViolationException if a constraint is violated during execution
		 * @throws gate.error.FKViolationException if a foreign key constraint is violated during execution
		 * @throws gate.error.UKViolationException if a unique key constraint is violated during execution
		 */
		int execute(Collection<T> values) throws ConstraintViolationException,
							 FKViolationException,
							 UKViolationException;

		/**
		 * Inserts the defined object on the database.
		 *
		 * @param value object to be inserted on the database
		 *
		 * @return the number of records affected by the insert sentence
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
		 * Inserts the defined objects on the database.
		 *
		 * @param values objects to be inserted on the database
		 *
		 * @return the number of records affected by the insert sentence
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
