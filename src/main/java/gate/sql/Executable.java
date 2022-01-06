package gate.sql;

import gate.error.ConstraintViolationException;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public interface Executable
{

	/**
	 * Creates a command for the statement.
	 *
	 * @return the new command created
	 */
	Command createCommand();

	/**
	 * Executes the sentence on the database with the specified parameters.
	 *
	 * @return the number of records affected by the sentence execution
	 *
	 * @throws gate.error.ConstraintViolationException if any database
	 * constraint is violated during sentence execution
	 */
	int execute() throws ConstraintViolationException;

	/**
	 * Executes the sentence on the database with the specified parameters
	 * and throws an exception if no record was affected.
	 *
	 * @param <T> type of the Exception to be thrown
	 * @param supplier of the exception to be thrown
	 * @throws gate.error.ConstraintViolationException if any database
	 * constraint is violated during sentence execution
	 * @throws T if no record was affected
	 */
	default <T extends Exception> void orElseThrow(Supplier<T> supplier)
		throws ConstraintViolationException, T
	{
		if (execute() == 0)
			throw supplier.get();
	}

	/**
	 * Executes the sentence on the database with the first specified list
	 * of parameters and fetches the generated key as an object of the
	 * specified type.
	 *
	 * @param <K> type of the key to be fetched
	 * @param type type of the key to be fetched
	 *
	 * @return the generated key as objects of the specified type
	 *
	 * @throws gate.error.ConstraintViolationException if any database
	 * constraint is violated during sentence execution
	 */
	<K> Optional<K> fetchGeneratedKey(Class<K> type) throws ConstraintViolationException;

	/**
	 * Executes the sentence on the database with the first specified list
	 * of parameters and fetches generated keys as objects of the specified
	 * type.
	 *
	 * @param <K> type of the keys to be fetched
	 * @param type type of the keys to be fetched
	 *
	 * @return any generated keys as objects of the specified type
	 *
	 * @throws gate.error.ConstraintViolationException if any database
	 * constraint is violated during sentence execution
	 */
	<K> List<K> fetchGeneratedKeys(Class<K> type) throws ConstraintViolationException;

	/**
	 * Executes the sentence on the database with the specified parameters
	 * and fetches generated keys as objects of the specified type.
	 *
	 * @param <K> type of the keys to be fetched
	 * @param type type of the keys to be fetched
	 * @param consumer the consumer to be used to process the fetched keys
	 *
	 * @throws gate.error.ConstraintViolationException if any database
	 * constraint is violated during sentence execution
	 */
	<K> void fetchGeneratedKey(Class<K> type, BiConsumer<List<?>, K> consumer) throws ConstraintViolationException;

	/**
	 * Executes the sentence on the database with the specified parameters
	 * and fetches generated keys as objects of the specified type.
	 *
	 * @param <K> type of the keys to be fetched
	 * @param type type of the keys to be fetched
	 * @param consumer the consumer to be used to process the fetched keys
	 *
	 * @throws gate.error.ConstraintViolationException if any database
	 * constraint is violated during sentence execution
	 */
	<K> void fetchGeneratedKeys(Class<K> type, BiConsumer<List<?>, List<K>> consumer) throws ConstraintViolationException;
}
