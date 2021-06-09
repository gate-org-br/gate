package gate.sql.statement;

import gate.error.ConstraintViolationException;
import gate.error.FKViolationException;
import gate.error.UKViolationException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Context to describe deletion criteria and values to be deleted.
 *
 * 
 */
public interface DeleteOperation<T>
{

	/**
	 * Deletes the defined objects from the database matching the id.
	 *
	 * @param values objects to be deleted from the database
	 *
	 * @return the number of records affected by the delete sentence
	 *
	 * @throws gate.error.ConstraintViolationException if a constraint is violated during execution
	 * @throws gate.error.FKViolationException         if a foreign key constraint is violated during execution
	 * @throws gate.error.UKViolationException         if a unique key constraint is violated during execution
	 */
	int execute(Collection<T> values) throws ConstraintViolationException,
			FKViolationException,
			UKViolationException;

	/**
	 * Deletes the defined object from the database matching the id.
	 *
	 * @param value object to be deleted from the database
	 *
	 * @return the number of records affected by the delete sentence
	 *
	 * @throws gate.error.ConstraintViolationException if a constraint is violated during execution
	 * @throws gate.error.FKViolationException         if a foreign key constraint is violated during execution
	 * @throws gate.error.UKViolationException         if a unique key constraint is violated during execution
	 */
	default int execute(T value) throws ConstraintViolationException,
			FKViolationException,
			UKViolationException
	{
		return execute(Collections.singleton(value));
	}

	/**
	 * Deletes the defined objects from the database matching the id.
	 *
	 * @param values objects to be deleted from the database
	 *
	 * @return the number of records affected by the delete sentence
	 *
	 * @throws gate.error.ConstraintViolationException if a constraint is violated during execution
	 * @throws gate.error.FKViolationException         if a foreign key constraint is violated during execution
	 * @throws gate.error.UKViolationException         if a unique key constraint is violated during execution
	 */
	default int execute(T... values) throws ConstraintViolationException,
			FKViolationException,
			UKViolationException
	{
		return DeleteOperation.this.execute(Arrays.asList(values));
	}

	/**
	 * Define the properties to be used as deletion criteria
	 *
	 * @param properties properties to be used as deletion criteria
	 *
	 * @return a Parameters object to be used to define the objects to be deleted
	 */
	Criteria<T> criteria(String... properties);

	/**
	 * Context to define the objects to be deleted from the database.
	 *
	 * 
	 */
	interface Criteria<T>
	{

		/**
		 * Deletes the defined objects from the database matching the selected criteria.
		 *
		 * @param values objects to be deleted from the database
		 *
		 * @return the number of records affected by the delete sentence
		 *
		 * @throws gate.error.ConstraintViolationException if a constraint is violated during execution
		 * @throws gate.error.FKViolationException         if a foreign key constraint is violated during execution
		 * @throws gate.error.UKViolationException         if a unique key constraint is violated during execution
		 */
		int execute(Collection<T> values) throws ConstraintViolationException,
				FKViolationException,
				UKViolationException;

		/**
		 * Deletes the defined object from the database matching the selected criteria.
		 *
		 * @param value object to be deleted from the database
		 *
		 * @return the number of records affected by the delete sentence
		 *
		 * @throws gate.error.ConstraintViolationException if a constraint is violated during execution
		 * @throws gate.error.FKViolationException         if a foreign key constraint is violated during execution
		 * @throws gate.error.UKViolationException         if a unique key constraint is violated during execution
		 */
		default int execute(T value) throws ConstraintViolationException,
				FKViolationException,
				UKViolationException
		{
			return execute(Collections.singleton(value));
		}

		/**
		 * Deletes the defined objects from the database matching the selected criteria.
		 *
		 * @param values objects to be deleted from the database
		 *
		 * @return the number of records affected by the delete sentence
		 *
		 * @throws gate.error.ConstraintViolationException if a constraint is violated during execution
		 * @throws gate.error.FKViolationException         if a foreign key constraint is violated during execution
		 * @throws gate.error.UKViolationException         if a unique key constraint is violated during execution
		 */
		default int execute(T... values) throws ConstraintViolationException,
				FKViolationException,
				UKViolationException
		{
			return Criteria.this.execute(Arrays.asList(values));
		}
	}
}
